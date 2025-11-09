package com.amuzil.magus.physics.core;

import com.amuzil.magus.physics.modules.IPhysicsModule;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;

import java.util.ArrayList;
import java.util.List;

public class ForceCloud extends PhysicsElement {

    private final List<ForcePoint> points;
    private final List<IPhysicsModule> modules;
    private double[] rotation;
    private final double cellSize;
    private ForceGrid<ForcePoint> spaceGrid;


    public ForceCloud(int type) {
        super(type);
        this.rotation = new double[4];
        this.points = new ArrayList<>();
        this.modules = new ArrayList<>();
        this.cellSize = 0.10;
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

    public void tick() {
        for (IPhysicsModule module : modules) {
            module.preSolve(this);
            module.solve(this);
            module.postSolve(this);
        }
    }

    /**
     * Build a uniform voxel grid of sampled vectors.
     * This is the vector field based on the current ForcePoints in the cloud. 
     *
     * @param centreX world-space centre X
     * @param centreY world-space centre Y
     * @param centreZ world-space centre Z
     * @param sizeX   number of cells along X
     * @param sizeY   number of cells along Y
     * @param sizeZ   number of cells along Z
     * @param cellDim world-space cell edge length
     * @return 3D array of Vec3 field samples
     */
    public Vec3[][][] buildVectorField(double centreX, double centreY, double centreZ,
                                       int sizeX, int sizeY, int sizeZ,
                                       double cellDim) {
        Vec3[][][] field = new Vec3[sizeX][sizeY][sizeZ];

        // ensure our grid is up-to-date
        rebuildSpatialGrid();

        // half-cell offset for sampling at cell centers
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

                    // grab only points in the neighboring cell block
                    List<ForcePoint> neighbors = spaceGrid.queryNeighbors(samplePos);

                    if (neighbors.isEmpty()) {
                        field[ix][iy][iz] = Vec3.ZERO;
                    } else {
                        // average their velocity (or force) vectors
                        Vec3 accum = Vec3.ZERO;
                        for (ForcePoint p : neighbors) {
                            accum = accum.add(p.vel());    // or p.force()
                        }
                        field[ix][iy][iz] = accum.scale(1.0 / neighbors.size());
                    }
                }
            }
        }
        return field;
    }

    public Vec3[][][] buildVectorField(Vec3 pos,
                                       int sizeX, int sizeY, int sizeZ,
                                       double cellDim) {
        return buildVectorField(pos.x, pos.y, pos.z, sizeX, sizeY, sizeZ, cellDim);
    }

    public Vec3[][][] buildVectorField(int sizeX, int sizeY, int sizeZ, double cellDim) {
        return buildVectorField(pos(), sizeX, sizeY, sizeZ, cellDim);
    }

    public void rebuildSpatialGrid() {
        // Build grid...
        if (spaceGrid == null)
            // Hash is hashed id + hashed pos
            spaceGrid = new ForceGrid<>(cellSize, hashCode());
        spaceGrid.clear();
        for (ForcePoint p : points) {
            spaceGrid.insert(p);               // uses p.pos() internally
        }
    }
}

