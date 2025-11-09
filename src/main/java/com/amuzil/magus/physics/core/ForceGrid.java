package com.amuzil.magus.physics.core;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicIntegerArray;

// A spatially hashed grid of forcepoints to prevent per-point neighbour checking. Yay!
public class ForceGrid<T extends IPhysicsElement> {
    private final double cellSize;
    private final int binCountX, binCountY, binCountZ;
    private final int totalBins;

    private final AtomicIntegerArray binCounts;
    private final int[] binStartOffsets;
    private final Object[] sortedPoints;    // store T references
    private final List<T> allPoints;

    private final ExecutorService threadPool;
    private final int parallelism;

    public ForceGrid(double cellSize, int binCountX, int binCountY, int binCountZ,
                     int maxPoints, ExecutorService threadPool) {
        this.cellSize = cellSize;
        this.binCountX = binCountX;
        this.binCountY = binCountY;
        this.binCountZ = binCountZ;
        this.totalBins = binCountX * binCountY * binCountZ;

        this.binCounts = new AtomicIntegerArray(totalBins);
        this.binStartOffsets = new int[totalBins];
        @SuppressWarnings("unchecked")
        Object[] arr = new Object[maxPoints];
        this.sortedPoints = arr;

        this.allPoints = new ArrayList<>(maxPoints);
        this.threadPool = threadPool;
        this.parallelism = Runtime.getRuntime().availableProcessors();
    }

    private int computeLinearBinIndex(long xi, long yi, long zi) {
        if (xi < 0 || xi >= binCountX ||
                yi < 0 || yi >= binCountY ||
                zi < 0 || zi >= binCountZ) {
            return -1;
        }
        return (int) (xi + yi * binCountX + zi * (binCountX * binCountY));
    }

    private long[] normalCoords(double x, double y, double z) {
        return new long[]{
                (long) Math.floor(x / cellSize),
                (long) Math.floor(y / cellSize),
                (long) Math.floor(z / cellSize)
        };
    }

    public void addPoint(T p) {
        allPoints.add(p);
    }

    public void clearPoints() {
        allPoints.clear();
    }

    public void rebuild() {
        final int n = allPoints.size();

        // Step 1: zero counts
        for (int i = 0; i < totalBins; i++) {
            binCounts.set(i, 0);
        }

        // Step 2: counting assignment (parallel)
        int chunkSize = (n + parallelism - 1) / parallelism;
        List<Future<?>> futures = new ArrayList<>();
        for (int t = 0; t < parallelism; t++) {
            final int start = t * chunkSize;
            final int end = Math.min(n, (t + 1) * chunkSize);
            futures.add(threadPool.submit(() -> {
                for (int i = start; i < end; i++) {
                    T p = allPoints.get(i);
                    Vec3 pos = p.pos();
                    long[] coords = normalCoords(pos.x, pos.y, pos.z);
                    int bi = computeLinearBinIndex(coords[0], coords[1], coords[2]);
                    if (bi >= 0) {
                        binCounts.incrementAndGet(bi);
                    }
                }
            }));
        }
        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Step 3: prefix sums & reset counts for scatter
        int offset = 0;
        for (int i = 0; i < totalBins; i++) {
            int c = binCounts.get(i);
            binStartOffsets[i] = offset;
            offset += c;
            binCounts.set(i, 0);
        }

        // Step 4: scatter points (parallel)
        futures.clear();
        for (int t = 0; t < parallelism; t++) {
            final int start = t * chunkSize;
            final int end = Math.min(n, (t + 1) * chunkSize);
            futures.add(threadPool.submit(() -> {
                for (int i = start; i < end; i++) {
                    T p = allPoints.get(i);
                    Vec3 pos = p.pos();
                    long[] coords = normalCoords(pos.x, pos.y, pos.z);
                    int bi = computeLinearBinIndex(coords[0], coords[1], coords[2]);
                    if (bi >= 0) {
                        int idx = binStartOffsets[bi] + binCounts.getAndIncrement(bi);
                        sortedPoints[idx] = p;
                    }
                }
            }));
        }
        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
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
                        @SuppressWarnings("unchecked")
                        T p = (T) sortedPoints[idx];
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
}