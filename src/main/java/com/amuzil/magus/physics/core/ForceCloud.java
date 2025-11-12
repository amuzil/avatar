package com.amuzil.magus.physics.core;

import com.amuzil.magus.physics.PhysicsBuilder;
import com.amuzil.magus.physics.modules.IPhysicsModule;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ForceCloud extends PhysicsElement {

    private final List<ForcePoint> points;
    private final List<IPhysicsModule> modules;
    private final double cellSize;
    private final ForceGrid<ForcePoint> spaceGrid;
    private double[] rotation;
    private AABB bounds = new AABB(0, 0, 0, 0, 0, 0);
    private double remainingLifeSeconds = -1.0; // -1 = infinite
    private boolean hasLifetime = false;

    public ForceCloud(int type, int maxPoints, Vec3 pos, Vec3 vel, Vec3 force,
                      @Nullable ExecutorService pool) {
        super(type);
        this.rotation = new double[4];
        this.points = new ArrayList<>();
        this.modules = new ArrayList<>();
        this.cellSize = PhysicsBuilder.CELL_SIZE;
        this.spaceGrid = new ForceGrid<>(cellSize,
                (int) (PhysicsBuilder.GRID_SIZE / cellSize),
                (int) (PhysicsBuilder.GRID_SIZE / cellSize),
                (int) (PhysicsBuilder.GRID_SIZE / cellSize),
                maxPoints, (long) pos.x, (long) pos.y, (long) pos.z,
                pool);

        //Current Pos
        insert(pos, 0);
        // Prev pos
        insert(Vec3.ZERO, 1);
        // Current vel.
        // TODO: Forces and velocities applied here should apply to every point in the system.
        insert(vel, 2);
        // Prev force
        insert(Vec3.ZERO, 3);
        // Direction / Force / Acceleration
        insert(force, 4);
    }

    // static so ForceSystem can call it for cross-cloud collisions too
    public static void resolvePair(ForcePoint p, ForcePoint q,
                                   double restRadius,
                                   double stiffness,
                                   double dampingCoeff) {
        Vec3 xp = p.pos();
        Vec3 xq = q.pos();

        Vec3 delta = xp.subtract(xq);
        double dist = delta.length();

        if (dist <= 1e-6 || dist >= restRadius) {
            return;
        }

        Vec3 n = delta.scale(1.0 / dist);
        double penetration = restRadius - dist;

        // --- repulsion ---
        double fMag = stiffness * penetration;
        Vec3 f = n.scale(fMag);

        // apply equal and opposite forces
        Vec3 fp = p.force().add(f);
        Vec3 fq = q.force().subtract(f);
        p.insert(fp, 4);
        q.insert(fq, 4);

        // --- damping along normal ---
        Vec3 vp = p.vel();
        Vec3 vq = q.vel();
        Vec3 relVel = vp.subtract(vq);
        double vn = relVel.dot(n);

        if (vn < 0.0) {
//            System.out.println("Attempting Collision.");
            double fdMag = -dampingCoeff * vn;
            Vec3 fd = n.scale(fdMag);

            fp = p.force().add(fd);
            fq = q.force().subtract(fd);
            p.insert(fp, 4);
            q.insert(fq, 4);
        }
    }

    // lifetime API
    public void setLifetimeSeconds(double seconds) {
        this.remainingLifeSeconds = seconds;
        this.hasLifetime = true;
    }

    public void tickLifetime(double dt) {
        if (!hasLifetime) return;
        remainingLifeSeconds -= dt;
    }

    public double lifetime() {
        return this.remainingLifeSeconds;
    }

    public boolean isDead() {
        return !hasLifetime || remainingLifeSeconds <= 0.0;
    }

    public void updateBoundsFromPoints() {
        if (points.isEmpty()) {
            this.bounds = new AABB(0, 0, 0, 0, 0, 0);
            return;
        }

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (ForcePoint p : points) {
            Vec3 pos = p.pos();
            if (pos.x < minX) minX = pos.x;
            if (pos.y < minY) minY = pos.y;
            if (pos.z < minZ) minZ = pos.z;
            if (pos.x > maxX) maxX = pos.x;
            if (pos.y > maxY) maxY = pos.y;
            if (pos.z > maxZ) maxZ = pos.z;
        }

        this.bounds = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public ForceGrid<ForcePoint> grid() {
        return spaceGrid;
    }

    public void addPoints(ForcePoint... points) {
        this.points.addAll(List.of(points));
    }

    public void addPoints(List<ForcePoint> points) {
        this.points.addAll(points);
    }

    public void deletePoints(ForcePoint... points) {
        this.points.removeAll(List.of(points));
    }

    public void deletePoints(List<ForcePoint> points) {
        this.points.removeAll(points);
    }

    public void clear() {
        this.points.clear();
    }

    public List<ForcePoint> pointsCopy() {
        return new ArrayList<>(this.points);
    }

    public List<ForcePoint> points() {
        return this.points;
    }

    public void mod(IPhysicsModule... modules) {
        this.modules.addAll(List.of(modules));
    }

    public List<IPhysicsModule> modules() {
        return this.modules;
    }

    public void rotate(double[] rotation) {
        // De-reference the original array, we do not want to be affecting it.
        this.rotation = rotation.clone();
    }

    public void rotate(Quaterniond rotation) {
        rotate(new double[]{rotation.x, rotation.y, rotation.z, rotation.w});
    }

    public double[] rotation() {
        return this.rotation;
    }

    public Quaterniond rot() {
        return new Quaterniond(rotation[0], rotation[1], rotation[2], rotation[3]);
    }

    // We only want copies passed
    public void writeHeader() {
        for (ForcePoint point : points())
            System.arraycopy(header, 0, point.header, 0, header.length);
    }

    public void updatePhysicsElement(double dt, IPhysicsElement element, Vec3 basePos) {
        Vec3 pos = element.pos();
        Vec3 vel = element.vel();
        Vec3 oldVel = vel;
        Vec3 force = element.force();

        double invMass = mass() > 0.0 ? 1.0 / mass() : 0.0;
        Vec3 acc = force.scale(invMass);

        // velocity update
        vel = element.newVel(dt, 1f);

        // simple damping
        double d = damping();
        if (d > 0.0) {
            vel = vel.scale(Math.max(0.0, 1.0 - d * dt));
        }

        Vec3 newPos = newPos(dt);

        // write back into the point's data columns:
        // 0 = pos, 1 = prevPos, 2 = vel, 3 = prevVel, 4 = force
        element.insert(pos, 1);        // prevPos
        element.insert(oldVel, 3);        // prevVel
        element.insert(newPos, 0);     // pos
        element.insert(vel, 2);
        element.insert(Vec3.ZERO, 4);  // clear force
    }
    public void tick(double dt) {
//        for (IPhysicsModule module : modules) {
//            module.preSolve(this);
//            module.solve(this);
//            module.postSolve(this);
//        }

        // Moves
        updatePhysicsElement(dt, this);
        // lifetime countdown
        tickLifetime(dt * 3);

        integratePoints(dt);

        updateBoundsFromPoints();
    }

    public AABB bounds() {
        return bounds;
    }

    public boolean boundsOverlap(ForceCloud other) {
        return this.bounds.intersects(other.bounds);
    }

    /**
     * Samples a 3D vector field from this cloud's points using its spatial grid.
     * <p>
     * Assumes {@code spaceGrid} has already been rebuilt for this tick.
     *
     * @param centreX world-space centre X of the sampling volume
     * @param centreY world-space centre Y of the sampling volume
     * @param centreZ world-space centre Z of the sampling volume
     * @param sizeX   number of samples along X
     * @param sizeY   number of samples along Y
     * @param sizeZ   number of samples along Z
     * @param cellDim world-space spacing between samples (edge length)
     * @param radius  neighbour radius to query around each sample position
     * @return 3D array of sampled vectors (e.g. average velocity)
     */
    public Vec3[][][] buildVectorField(double centreX, double centreY, double centreZ,
                                       int sizeX, int sizeY, int sizeZ,
                                       double cellDim,
                                       double radius) {
        Vec3[][][] field = new Vec3[sizeX][sizeY][sizeZ];

        // If we have no grid or no points, just return zeros.
        if (spaceGrid == null || points.isEmpty()) {
            for (int ix = 0; ix < sizeX; ix++) {
                for (int iy = 0; iy < sizeY; iy++) {
                    for (int iz = 0; iz < sizeZ; iz++) {
                        field[ix][iy][iz] = Vec3.ZERO;
                    }
                }
            }
            return field;
        }

        // Sample at cell centres
        double half = cellDim * 0.5;

        double halfGridX = (sizeX * cellDim) * 0.5;
        double halfGridY = (sizeY * cellDim) * 0.5;
        double halfGridZ = (sizeZ * cellDim) * 0.5;

        double minX = centreX - halfGridX;
        double minY = centreY - halfGridY;
        double minZ = centreZ - halfGridZ;

        for (int ix = 0; ix < sizeX; ix++) {
            double x = minX + ix * cellDim + half;
            for (int iy = 0; iy < sizeY; iy++) {
                double y = minY + iy * cellDim + half;
                for (int iz = 0; iz < sizeZ; iz++) {
                    double z = minZ + iz * cellDim + half;
                    Vec3 samplePos = new Vec3(x, y, z);

                    List<ForcePoint> neighbours = spaceGrid.queryRadius(samplePos, radius);

                    if (neighbours.isEmpty()) {
                        field[ix][iy][iz] = Vec3.ZERO;
                    } else {
                        Vec3 accum = Vec3.ZERO;
                        for (ForcePoint p : neighbours) {
                            // decide here whether you want velocity, force, or some combo
                            accum = accum.add(p.vel()); // or p.force()
                        }
                        field[ix][iy][iz] = accum.scale(1.0 / neighbours.size());
                    }
                }
            }
        }

        return field;
    }

    // Recommended default: sample roughly within a cell + neighbours.
    public Vec3[][][] buildVectorField(double centreX, double centreY, double centreZ,
                                       int sizeX, int sizeY, int sizeZ,
                                       double cellDim) {
        // sqrt(3)/2 ~= 0.866 → covers cell centre to corner in 3D
        double radius = cellDim * Math.sqrt(3.0) * 0.5;
        return buildVectorField(centreX, centreY, centreZ, sizeX, sizeY, sizeZ, cellDim, radius);
    }

    public Vec3[][][] buildVectorField(Vec3 centre,
                                       int sizeX, int sizeY, int sizeZ,
                                       double cellDim) {
        return buildVectorField(centre.x, centre.y, centre.z, sizeX, sizeY, sizeZ, cellDim);
    }

    public Vec3[][][] buildVectorField(Vec3 centre,
                                       int sizeX, int sizeY, int sizeZ,
                                       double cellDim,
                                       double radius) {
        return buildVectorField(centre.x, centre.y, centre.z, sizeX, sizeY, sizeZ, cellDim, radius);
    }

    // Test Physics

    public Vec3[][][] buildVectorField(int sizeX, int sizeY, int sizeZ, double cellDim) {
        return buildVectorField(pos(), sizeX, sizeY, sizeZ, cellDim);
    }

    private void integratePoints(double dt) {
        for (ForcePoint p : points) {
            Vec3 pos = p.pos();
            Vec3 vel = p.vel();
            Vec3 oldVel = vel;
            Vec3 force = p.force();

            double invMass = p.mass() > 0.0 ? 1.0 / p.mass() : 0.0;
            Vec3 acc = force.scale(invMass);

            // velocity update
            vel = newVel(dt, 1f);

            // simple damping
            double d = p.damping();
            if (d > 0.0) {
                vel = vel.scale(Math.max(0.0, 1.0 - d * dt));
            }

            Vec3 newPos = newPos(dt);

            // write back into the point's data columns:
            // 0 = pos, 1 = prevPos, 2 = vel, 3 = prevVel, 4 = force
            p.insert(pos, 1);        // prevPos
            p.insert(oldVel, 3);        // prevVel
            p.insert(newPos, 0);     // pos
            p.insert(vel, 2);
            p.insert(Vec3.ZERO, 4);  // clear force
        }
    }

    public void resolveSelfCollisions(double restRadius,
                                      double stiffness,
                                      double dampingCoeff) {
        if (points.isEmpty()) return;

        double r2 = restRadius * restRadius;
        int n = points.size();

//        for (int i = 0; i < n; i++) {
//            ForcePoint p = points.get(i);
//            Vec3 xp = p.pos();
//
//            for (int j = i + 1; j < n; j++) {
//                ForcePoint q = points.get(j);
//                Vec3 xq = q.pos();
//
//                double dx = xp.x - xq.x;
//                double dy = xp.y - xq.y;
//                double dz = xp.z - xq.z;
//                double dist2 = dx * dx + dy * dy + dz * dz;
//
//                if (dist2 > r2 || dist2 <= 1e-12) {
//                    continue;
//                }
//
//                resolvePair(p, q, restRadius, stiffness, dampingCoeff);
//            }
//        }

        // Alternative implementation using spatial grid (commented out due to inefficiency with small clouds)
        if (points.isEmpty()) return;

        for (ForcePoint p : points) {
            List<ForcePoint> neighbours = spaceGrid.queryRadius(p.pos(), restRadius);
            for (ForcePoint q : neighbours) {
                if (q == p) continue;
                // cheap symmetry break to avoid double work
//                if (System.identityHashCode(q) <= System.identityHashCode(p)) continue;
                resolvePair(p, q, restRadius, stiffness, dampingCoeff);
            }
        }
    }


    // Don't need this anymore... Rebuild is called from within the manager for the overall SpaceGrid
    public void rebuildSpatialGrid() {
        // Build grid...
        if (points.isEmpty()) {
            spaceGrid.rebuildFrom(points); // clears used bins
            return;
        }

        // Optionally reposition grid origin around this cloud’s bounds:
        // e.g. snap to min corner in blocks
        long originX = (long) Math.floor(bounds.minX);
        long originY = (long) Math.floor(bounds.minY);
        long originZ = (long) Math.floor(bounds.minZ);
        spaceGrid.setOrigin(originX, originY, originZ);

        // Now rebuild from our point list
        spaceGrid.rebuildFrom(points);
    }
}

