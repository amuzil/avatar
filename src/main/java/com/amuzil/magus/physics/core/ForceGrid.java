package com.amuzil.magus.physics.core;

import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

// A spatially hashed grid of forcepoints to prevent per-point neighbour checking. Yay!
public class ForceGrid<T extends IPhysicsElement> {
    public static final int MIN_POINTS_PER_WORKER = 4096; // keep for future parallelism if you want

    private final double cellSize;
    private final int binCountX, binCountY, binCountZ;
    private final int totalBins;

    // Sparse bins: each bin is a list of points (lazy-created)
    private final List<T>[] bins;
    // Track which bins are non-empty this frame
    private final int[] usedBins;
    private final List<T> allPoints;
    private final ExecutorService threadPool;
    private final int parallelism;
    private int usedBinCount = 0;
    private long originX, originY, originZ;

    @SuppressWarnings("unchecked")
    public ForceGrid(double cellSize, int binCountX, int binCountY, int binCountZ,
                     int maxPoints, long originX, long originY, long originZ, @Nullable ExecutorService threadPool) {
        this.cellSize = cellSize;
        this.binCountX = binCountX;
        this.binCountY = binCountY;
        this.binCountZ = binCountZ;
        this.totalBins = binCountX * binCountY * binCountZ;

        this.originX = originX;
        this.originY = originY;
        this.originZ = originZ;

        this.bins = (List<T>[]) new List[totalBins];
        this.usedBins = new int[totalBins]; // worst-case: all bins used once this frame

        this.allPoints = new ArrayList<>(maxPoints);
        this.threadPool = threadPool;
        this.parallelism = Math.max(Runtime.getRuntime().availableProcessors() / 2, 1);
    }

    private int computeLinearBinIndex(long xi, long yi, long zi) {
        if (xi < 0 || xi >= binCountX ||
                yi < 0 || yi >= binCountY ||
                zi < 0 || zi >= binCountZ) {
            return -1;
        }
        return (int) (xi + yi * binCountX + zi * (binCountX * binCountY));
    }

    private void normalCoords(double x, double y, double z, long[] coords) {
        double lx = x - originX; // world → local
        double ly = y - originY;
        double lz = z - originZ;

        coords[0] = (long) Math.floor(lx / cellSize);
        coords[1] = (long) Math.floor(ly / cellSize);
        coords[2] = (long) Math.floor(lz / cellSize);
    }

    private long[] normalCoords(double x, double y, double z) {
        long[] coords = new long[3];
        normalCoords(x, y, z, coords);
        return coords;
    }

    public void insert(T p) {
        allPoints.add(p);
    }

    public void clear() {
        allPoints.clear();
        // bins will be cleared lazily in rebuild()
    }


    /**
     * Rebuild the spatial grid from the given list of points.
     * Assumes originX/Y/Z and binCountX/Y/Z are already set appropriately.
     */
    public void rebuildFrom(List<T> points) {
        // 1) Clear previously used bins
        for (int i = 0; i < usedBinCount; i++) {
            int bi = usedBins[i];
            List<T> list = bins[bi];
            if (list != null) {
                list.clear();
            }
        }
        usedBinCount = 0;

        if (points.isEmpty()) {
            return;
        }

        long[] coords = new long[3];

        // 2) Insert each point
        for (int i = 0, n = points.size(); i < n; i++) {
            T p = points.get(i);
            if (p == null) continue;

            Vec3 pos = p.pos(); // world-space
            normalCoords(pos.x, pos.y, pos.z, coords);

            int bi = computeLinearBinIndex(coords[0], coords[1], coords[2]);
            if (bi < 0) {
                // outside grid bounds; silently skip
                continue;
            }

            List<T> list = bins[bi];
            if (list == null) {
                list = new ArrayList<>();
                bins[bi] = list;
            }
            if (list.isEmpty()) {
                usedBins[usedBinCount++] = bi;
            }
            list.add(p);
        }
    }
    /**
     * Rebuild the spatial grid from allPoints.
     * Complexity: O(numPoints + usedBins) instead of O(totalBins).
     */
    public void rebuild() {
        rebuildFrom(allPoints);
//        // Take a snapshot so we don't care if allPoints is mutated elsewhere
//        final List<T> snapshot = new ArrayList<>(allPoints);
//        final int n = snapshot.size();
//
//        if (n == 0) {
//            // Clear only bins that were used last frame
//            for (int i = 0; i < usedBinCount; i++) {
//                int bi = usedBins[i];
//                List<T> list = bins[bi];
//                if (list != null) {
//                    list.clear();
//                }
//            }
//            usedBinCount = 0;
//            return;
//        }
//
//        // 1) Clear previously used bins
//        for (int i = 0; i < usedBinCount; i++) {
//            int bi = usedBins[i];
//            List<T> list = bins[bi];
//            if (list != null) {
//                list.clear();
//            }
//        }
//        usedBinCount = 0;
//
//        // 2) Re-insert all points from the snapshot into appropriate bins
//        long[] coords = new long[3];
//        for (int i = 0; i < n; i++) {
//            T p = snapshot.get(i);
//            if (p == null) {
//                continue; // paranoia
//            }
//            Vec3 pos = p.pos();
//            normalCoords(pos.x, pos.y, pos.z, coords);
//            int bi = computeLinearBinIndex(coords[0], coords[1], coords[2]);
//            if (bi < 0) continue;
//
//            List<T> list = bins[bi];
//            if (list == null) {
//                list = new ArrayList<>();
//                bins[bi] = list;
//            }
//            if (list.isEmpty()) {
//                usedBins[usedBinCount++] = bi;
//            }
//            list.add(p);
//        }
    }

    public List<T> queryRadius(Vec3 pos, double radius) {
        List<T> result = new ArrayList<>();
        long[] baseC = normalCoords(pos.x, pos.y, pos.z);
        int cellRadius = (int) Math.ceil(radius / cellSize);
        double r2 = radius * radius;

        for (int dz = -cellRadius; dz <= cellRadius; dz++) {
            for (int dy = -cellRadius; dy <= cellRadius; dy++) {
                for (int dx = -cellRadius; dx <= cellRadius; dx++) {
                    long xi = baseC[0] + dx;
                    long yi = baseC[1] + dy;
                    long zi = baseC[2] + dz;
                    int bi = computeLinearBinIndex(xi, yi, zi);
                    if (bi < 0) continue;

                    List<T> list = bins[bi];
                    if (list == null || list.isEmpty()) continue;

                    for (T p : list) {
                        Vec3 pp = p.pos();
                        double dxp = pp.x - pos.x;
                        double dyp = pp.y - pos.y;
                        double dzp = pp.z - pos.z;
                        if (dxp * dxp + dyp * dyp + dzp * dzp <= r2) {
                            result.add(p);
                        }
                    }
                }
            }
        }
        return result;
    }

    // Used for centroid
    public List<T> queryCell(Vec3 pos) {
        List<T> result = new ArrayList<>();
        long[] baseC = normalCoords(pos.x, pos.y, pos.z);

        long xi = baseC[0];
        long yi = baseC[1];
        long zi = baseC[2];
        int bi = computeLinearBinIndex(xi, yi, zi);
        if (bi < 0) {
            return result;
        }

        List<T> list = bins[bi];
        if (list == null || list.isEmpty()) {
            return result;
        }

        // You had an r2 check before; keep a small radius if you want
        double r2 = 1.0;
        for (T p : list) {
            Vec3 pp = p.pos();
            double dxp = pp.x - pos.x;
            double dyp = pp.y - pos.y;
            double dzp = pp.z - pos.z;
            if (dxp * dxp + dyp * dyp + dzp * dzp <= r2) {
                result.add(p);
            }
        }
        return result;
    }

    public @Nullable Vec3 queryCellCentroid(Vec3 pos) {
        List<T> cellPoints = queryCell(pos);
        if (cellPoints.isEmpty()) {
            return null;
        }
        double sumX = 0;
        double sumY = 0;
        double sumZ = 0;
        for (T p : cellPoints) {
            Vec3 pp = p.pos();
            sumX += pp.x;
            sumY += pp.y;
            sumZ += pp.z;
        }
        int count = cellPoints.size();
        return new Vec3(sumX / count, sumY / count, sumZ / count);
    }

    public void setOrigin(long x, long y, long z) {
        this.originX = x;
        this.originY = y;
        this.originZ = z;
    }
}