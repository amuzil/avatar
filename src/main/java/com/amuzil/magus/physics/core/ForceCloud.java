package com.amuzil.magus.physics.core;

import com.amuzil.magus.physics.PhysicsBuilder;
import com.amuzil.magus.physics.modules.IPhysicsModule;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ForceCloud extends PhysicsElement {

    private final List<ForcePoint> points;
    private final List<IPhysicsModule> modules;
    private final double cellSize;
    private double[] rotation;
    private final ForceGrid<ForcePoint> spaceGrid;


    public ForceCloud(int type, int maxPoints,
                      ExecutorService pool) {
        super(type);
        this.rotation = new double[4];
        this.points = new ArrayList<>();
        this.modules = new ArrayList<>();
        this.cellSize = PhysicsBuilder.CELL_SIZE;
        this.spaceGrid = new ForceGrid<ForcePoint>(cellSize,
                (int) (PhysicsBuilder.GRID_SIZE / cellSize),
                (int) (PhysicsBuilder.GRID_SIZE / cellSize),
                (int) (PhysicsBuilder.GRID_SIZE / cellSize),
                maxPoints,
                pool);
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


    public Vec3[][][] buildVectorField(int sizeX, int sizeY, int sizeZ, double cellDim) {
        return buildVectorField(pos(), sizeX, sizeY, sizeZ, cellDim);
    }

    // Don't need this anymore... Rebuild is called from within the manager for the overall SpaceGrid
    public void rebuildSpatialGrid() {
        // Build grid...
        if (spaceGrid == null) return;
        spaceGrid.clear();
        for (ForcePoint p : points) {
            spaceGrid.insert(p);
        }
        spaceGrid.rebuild();
    }
}

