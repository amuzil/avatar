package com.amuzil.omegasource.utils.physics.core;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// A spatially hashed grid of forcepoints to prevent per-point neighbour checking. Yay!
public class ForceGrid {
    private final double cellSize;
    private final Map<Long, List<ForcePoint>> cells = new HashMap<>();

    /**
     * @param cellSize edge length of each cubic cell in world units
     */
    public ForceGrid(double cellSize) {
        this.cellSize = cellSize;
    }

    /**
     * Compute a 64-bit key from world coordinates.
     */
    private long hashKey(double x, double y, double z) {
        long xi = (long) Math.floor(x / cellSize);
        long yi = (long) Math.floor(y / cellSize);
        long zi = (long) Math.floor(z / cellSize);
        // pack into 20 bits each (mask for safety if world is large)
        return ((xi & 0xFFFFF) << 40)
                | ((yi & 0xFFFFF) << 20)
                |  (zi & 0xFFFFF);
    }

    /**
     * Insert a point using its own position.
     */
    public void insert(ForcePoint p) {
        insert(p, p.pos());
    }

    /**
     * Insert a point into its corresponding cell based on position.
     */
    public void insert(ForcePoint p, Vec3 pos) {
        long key = hashKey(pos.x, pos.y, pos.z);
        cells.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
    }

    /**
     * Remove a point by reading its stored previous position.
     */
    public void remove(ForcePoint p) {
        remove(p, p.pos());
    }

    /**
     * Remove a point from the cell corresponding to the given position.
     */
    public void remove(ForcePoint p, Vec3 pos) {
        long key = hashKey(pos.x, pos.y, pos.z);
        List<ForcePoint> list = cells.get(key);
        if (list != null) {
            list.remove(p);
            if (list.isEmpty()) {
                cells.remove(key);
            }
        }
    }

    /**
     * Query all points in the 27-cell neighborhood (radius=1) around a given position.
     */
    public List<ForcePoint> queryNeighbors(Vec3 pos) {
        return queryNeighbors(pos, 1);
    }

    /**
     * Query all points within a cubic neighborhood of given radius (in cells).
     * @param pos        world-space sample position
     * @param cellRadius how many cells to extend in each axis (>=0)
     */
    public List<ForcePoint> queryNeighbors(Vec3 pos, int cellRadius) {
        List<ForcePoint> result = new ArrayList<>();
        long xi = (long) Math.floor(pos.x / cellSize);
        long yi = (long) Math.floor(pos.y / cellSize);
        long zi = (long) Math.floor(pos.z / cellSize);
        for (long dx = -cellRadius; dx <= cellRadius; dx++) {
            for (long dy = -cellRadius; dy <= cellRadius; dy++) {
                for (long dz = -cellRadius; dz <= cellRadius; dz++) {
                    long key = (((xi + dx) & 0xFFFFF) << 40)
                            | (((yi + dy) & 0xFFFFF) << 20)
                            |  ((zi + dz) & 0xFFFFF);
                    List<ForcePoint> bucket = cells.get(key);
                    if (bucket != null) {
                        result.addAll(bucket);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Clear all cells (call each frame before re-inserting if needed).
     */
    public void clear() {
        cells.clear();
    }
}
