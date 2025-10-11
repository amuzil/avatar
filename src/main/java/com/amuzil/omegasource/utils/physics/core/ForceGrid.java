package com.amuzil.omegasource.utils.physics.core;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// A spatially hashed grid of forcepoints to prevent per-point neighbour checking. Yay!
public class ForceGrid<T extends IPhysicsElement> {
    private final double cellSize;
    private final Map<Long, List<T>> cells = new HashMap<>();

    // Hash made from whatever; used to identify where each point came from.
    // Usually going to be id + hashed owner id.
    private long identifierHash;

    /**
     * @param cellSize edge length of each cubic cell in world units
     */
    public ForceGrid(double cellSize) {
        this.cellSize = cellSize;
    }

    public ForceGrid(double cellSize, long identifierHash) {
        this(cellSize);
        this.identifierHash = identifierHash;
    }

    /**
     * Compute a 64-bit key from world coordinates.
     */
    private long hashKey(double x, double y, double z) {
        long[] coords = normalCoords(x, y, z);
        return computeKey(coords[0], coords[1], coords[2]);
    }

    /**
     * @param x
     * @param y
     * @param z
     * @return Normalised array of world to hash coordinates before the actual key is computed.
     */
    private long[] normalCoords(double x, double y, double z) {
        return new long[]{
                (long) Math.floor(x / cellSize),
                (long) Math.floor(y / cellSize),
                (long) Math.floor(z / cellSize),
        };
    }

    private long[] normalCoords(Vec3 pos) {
        return normalCoords(pos.x, pos.y, pos.z);
    }

    private long computeKey(long xi, long yi, long zi) {
        // pack into 20 bits each (mask for safety if the world is large)
        long key = ((xi & 0xFFFFF) << 40)
                | ((yi & 0xFFFFF) << 20)
                | (zi & 0xFFFFF);
        return key ^ identifierHash;
    }


    /**
     * Insert a point using its own position.
     */
    public void insert(T p) {
        insert(p, p.pos());
    }

    /**
     * Insert a point into its corresponding cell based on position.
     */
    public void insert(T p, Vec3 pos) {
        long key = hashKey(pos.x, pos.y, pos.z);
        cells.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
    }

    /**
     * Remove a point by reading its stored previous position.
     */
    public void remove(T p) {
        remove(p, p.pos());
    }

    /**
     * Remove a point from the cell corresponding to the given position.
     */
    public void remove(T p, Vec3 pos) {
        long key = hashKey(pos.x, pos.y, pos.z);
        List<T> bucket = cells.get(key);
        if (bucket != null) {
            bucket.remove(p);
            if (bucket.isEmpty()) {
                cells.remove(key);
            }
        }
    }

    /**
     * Query all points in the 27-cell neighborhood (radius=1) around a given position.
     */
    public List<T> queryNeighbors(Vec3 pos) {
        return queryNeighbors(pos, 1);
    }

    /**
     * Query all points within a cubic neighborhood of given radius (in cells).
     *
     * @param pos        world-space sample position
     * @param cellRadius how many cells to extend in each axis (>=0)
     */
    public List<T> queryNeighbors(Vec3 pos, int cellRadius) {
        long[] coords = normalCoords(pos);
        List<T> result = new ArrayList<>();
        for (long dx = -cellRadius; dx <= cellRadius; dx++) {
            for (long dy = -cellRadius; dy <= cellRadius; dy++) {
                for (long dz = -cellRadius; dz <= cellRadius; dz++) {
                    long key = computeKey(coords[0] + dx, coords[1] + dy, coords[2] + dz);
                    List<T> bucket = cells.get(key);
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

    public long idHash() {
        return this.identifierHash;
    }
}
