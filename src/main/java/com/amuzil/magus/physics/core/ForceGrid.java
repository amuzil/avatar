package com.amuzil.magus.physics.core;

import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicIntegerArray;

// A spatially hashed grid of forcepoints to prevent per-point neighbour checking. Yay!
public class ForceGrid<T extends IPhysicsElement> {
    public static final int MIN_POINTS_PER_WORKER = 4096;

    private final double cellSize;
    private final int binCountX, binCountY, binCountZ;
    private final int totalBins;

    private final AtomicIntegerArray binCounts;
    private final int[] binStartOffsets;
    private final T[] sortedPoints;    // store T references
    private final List<T> allPoints;

    private final ExecutorService threadPool;
    private final int parallelism;
    private final List<Future<?>> futures = new ArrayList<>();

    public ForceGrid(double cellSize, int binCountX, int binCountY, int binCountZ,
                     int maxPoints, @Nullable ExecutorService threadPool) {
        this.cellSize = cellSize;
        this.binCountX = binCountX;
        this.binCountY = binCountY;
        this.binCountZ = binCountZ;
        this.totalBins = binCountX * binCountY * binCountZ;

        this.binCounts = new AtomicIntegerArray(totalBins);
        this.binStartOffsets = new int[totalBins];

        @SuppressWarnings("unchecked")
        T[] sp = (T[]) new IPhysicsElement[maxPoints];
        this.sortedPoints = sp;

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

    // Fills an existing coords array
    private void normalCoords(double x, double y, double z, long[] coords) {
        coords[0] = (long) Math.floor(x / cellSize);
        coords[1] = (long) Math.floor(y / cellSize);
        coords[2] = (long) Math.floor(z / cellSize);
    }

    // Convenience overload that allocates a new array
    private long[] normalCoords(double x, double y, double z) {
        long[] coords = new long[3];
        normalCoords(x, y, z, coords);
        return coords;
    }

    public void insert(T p) {
        allPoints.add(p);
        // you *may* want to guard against exceeding sortedPoints.length here
    }

    public void clear() {
        allPoints.clear();
    }

    public void rebuild() {
        final int n = allPoints.size();
        if (n == 0) {
            // Clear counts so queries don't see stale data
            for (int i = 0; i < totalBins; i++) {
                binCounts.set(i, 0);
                binStartOffsets[i] = 0;
            }
            return;
        }

        // Guard (optional): avoid overflowing sortedPoints
        final int effectiveN = Math.min(n, sortedPoints.length);

        // Step 1: zero counts
        for (int i = 0; i < totalBins; i++) {
            binCounts.set(i, 0);
        }

        boolean canParallel = (threadPool != null && parallelism > 1);
        boolean useParallel = canParallel && effectiveN >= MIN_POINTS_PER_WORKER;

        int numWorkers;
        if (useParallel) {
            // Aim for at least MIN_POINTS_PER_WORKER per worker
            numWorkers = Math.min(
                    parallelism,
                    Math.max(1, (effectiveN + MIN_POINTS_PER_WORKER - 1) / MIN_POINTS_PER_WORKER)
            );
        } else {
            numWorkers = 1;
        }

        int chunkSize = (effectiveN + numWorkers - 1) / numWorkers;

        // ---- Step 2: counting assignment ----
        if (useParallel) {
            futures.clear();
            for (int t = 0; t < numWorkers; t++) {
                final int start = t * chunkSize;
                if (start >= effectiveN) break;
                final int end = Math.min(effectiveN, (t + 1) * chunkSize);
                futures.add(threadPool.submit(() -> {
                    long[] coords = new long[3];
                    for (int i = start; i < end; i++) {
                        binPoints(coords, i);
                    }
                }));
            }
            waitAll(futures);
        } else {
            long[] coords = new long[3];
            for (int i = 0; i < effectiveN; i++) {
                binPoints(coords, i);
            }
        }

        // ---- Step 3: prefix sums & reset counts for scatter ----
        int offset = 0;
        for (int i = 0; i < totalBins; i++) {
            int c = binCounts.get(i);
            binStartOffsets[i] = offset;
            offset += c;
            binCounts.set(i, 0);
        }

        // ---- Step 4: scatter points ----
        if (useParallel) {
            futures.clear();
            for (int t = 0; t < numWorkers; t++) {
                final int start = t * chunkSize;
                if (start >= effectiveN) break;
                final int end = Math.min(effectiveN, (t + 1) * chunkSize);
                futures.add(threadPool.submit(() -> {
                    long[] coords = new long[3];
                    for (int i = start; i < end; i++) {
                        sortPoints(coords, i);
                    }
                }));
            }
            waitAll(futures);
        } else {
            long[] coords = new long[3];
            for (int i = 0; i < effectiveN; i++) {
                sortPoints(coords, i);
            }
        }
    }

    private void sortPoints(long[] coords, int i) {
        T p = allPoints.get(i);
        Vec3 pos = p.pos();
        normalCoords(pos.x, pos.y, pos.z, coords);
        int bi = computeLinearBinIndex(coords[0], coords[1], coords[2]);
        if (bi >= 0) {
            int idx = binStartOffsets[bi] + binCounts.getAndIncrement(bi);
            sortedPoints[idx] = p;
        }
    }

    private void binPoints(long[] coords, int i) {
        T p = allPoints.get(i);
        Vec3 pos = p.pos();
        normalCoords(pos.x, pos.y, pos.z, coords);
        int bi = computeLinearBinIndex(coords[0], coords[1], coords[2]);
        if (bi >= 0) {
            binCounts.incrementAndGet(bi);
        }
    }

    private void waitAll(List<Future<?>> futures) {
        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
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

                    int start = binStartOffsets[bi];
                    int count = binCounts.get(bi);
                    for (int idx = start; idx < start + count; idx++) {
                        T p = sortedPoints[idx];
                        if (p == null) continue; // paranoia
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
        int cellRadius = 1;
        double r2 = 1;

        long xi = baseC[0];
        long yi = baseC[1];
        long zi = baseC[2];
        int bi = computeLinearBinIndex(xi, yi, zi);
        if (bi < 0)
            return result;

        int start = binStartOffsets[bi];
        int count = binCounts.get(bi);
        for (int idx = start; idx < start + count; idx++) {
            T p = sortedPoints[idx];
            if (p == null) return result; // paranoia
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
}