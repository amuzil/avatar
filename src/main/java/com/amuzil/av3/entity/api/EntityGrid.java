package com.amuzil.av3.entity.api;

import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.renderer.mc.DCStitcher;
import com.amuzil.av3.renderer.mc.Vertex;
import com.amuzil.magus.physics.core.ForceElement;
import com.amuzil.magus.physics.core.ForceGrid;
import com.amuzil.magus.physics.core.ForcePhysicsElement;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

// Copied from ForceGrid but adapted for AvatarEntity
public class EntityGrid<T extends AvatarEntity> {
    public static final int MIN_POINTS_PER_WORKER = 4096; // keep for future parallelism if you want

    private final double cellSize;
    private final int binCountX, binCountY, binCountZ;
    private final int totalBins;

    // Sparse bins: each bin is a list of points (lazy-created)
    private final List<T>[] bins;
    // Track which bins are non-empty this frame
    private final int[] usedBins;
    private final List<T> allPoints;

    // Threading one day...
    private final ExecutorService threadPool;
    private final int parallelism;
    private final int maxPoints;
    ForceGrid.NormalProvider<ForceElement> velNormal = pe -> {
        Vec3 v = pe.vel(); // or whatever you call it
        return new Vector3f((float) v.x, (float) v.y, (float) v.z);
    };
    private int usedBinCount = 0;
    private long originX, originY, originZ;

    @SuppressWarnings("unchecked")
    public EntityGrid(double cellSize, int binCountX, int binCountY, int binCountZ, int maxPoints, long originX, long originY, long originZ, @Nullable ExecutorService threadPool) {
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

        this.maxPoints = maxPoints;
        this.allPoints = new ArrayList<>(maxPoints);
        this.threadPool = threadPool;
        this.parallelism = Math.max(Runtime.getRuntime().availableProcessors() / 2, 1);
    }

    public int maxPoints() {
        return maxPoints;
    }

    public Vec3 origin() {
        return new Vec3(originX, originY, originZ);
    }

    public float cellSize() {
        return (float) this.cellSize;
    }

    public int binX() {
        return binCountX;
    }

    public int binY() {
        return binCountY;
    }

    public int binZ() {
        return binCountZ;
    }

    private int computeLinearBinIndex(long xi, long yi, long zi) {
        if (xi < 0 || xi >= binCountX || yi < 0 || yi >= binCountY || zi < 0 || zi >= binCountZ) {
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

    public void clearUsedBins() {
        for (int i = 0; i < usedBinCount; i++) {
            int bi = usedBins[i];
            List<T> list = bins[bi];
            if (list != null) {
                list.clear();
            }
        }
        usedBinCount = 0;
    }

    /**
     * Rebuild the spatial grid from the given list of points.
     * Assumes originX/Y/Z and binCountX/Y/Z are already set appropriately.
     */
    public void rebuildFrom(List<T> points) {
        // 1) Clear previously used bins
        clearUsedBins();

        if (points.isEmpty()) {
            return;
        }

        long[] coords = new long[3];

        // 2) Insert each point
        for (int i = 0, n = points.size(); i < n; i++) {
            T p = points.get(i);
            if (p == null) continue;

            Vec3 pos = p.position(); // world-space
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

        // 3) mark relevant points
        for (int i = 0, n = points.size(); i < n; i++) {
            T p = points.get(i);
            if (p == null) continue;
            Vec3 pos = p.position(); // world-space
            normalCoords(pos.x, pos.y, pos.z, coords);

            int bi = computeLinearBinIndex(coords[0], coords[1], coords[2]);

            if (isSurfaceBin(bi) && p instanceof AvatarEntity) {
                p.surface(true);
            }
        }
    }

    /**
     * Rebuild the spatial grid from allPoints.
     * Complexity: O(numPoints + usedBins) instead of O(totalBins).
     */
    public void rebuild() {
        rebuildFrom(allPoints);
    }

    private void coordsFromIndex(int bi, int[] outXYZ) {
        int BXY = binCountX * binCountY;
        int z = bi / BXY;
        int r = bi % BXY;
        int y = r / binCountX;
        int x = r % binCountX;
        outXYZ[0] = x;
        outXYZ[1] = y;
        outXYZ[2] = z;
    }

    private boolean binEmpty(int bi) {
        if (bi < 0) return true;
        List<T> l = bins[bi];
        return l == null || l.isEmpty();
    }

    public Vector3f[] surfaceGridForDC() {
        Vector3f[] dcPos = new Vector3f[totalBins];

        // Only iterate usedBins so we don't touch empty cells unnecessarily.
        for (int i = 0; i < usedBinCount; i++) {
            int bi = usedBins[i];
            T elem = surfaceElementInBin(bi);
            if (elem == null) continue;

            Vec3 pos = elem.position(); // world-space
            dcPos[bi] = new Vector3f((float) pos.x, (float) pos.y, (float) pos.z);
        }

        return dcPos;
    }

    private @Nullable T surfaceElementInBin(int bi) {
        if (bi < 0 || bi >= totalBins) return null;

        List<T> bin = bins[bi];
        if (bin == null || bin.isEmpty()) return null;

        for (T p : bin) {
            if (p instanceof AvatarEntity pe && pe.surface()) {
                return p;
            }
        }
        return null;
    }

    private boolean isSurfaceBin(int bi) {
        if (binEmpty(bi)) return false;
        int[] xyz = new int[3];
        coordsFromIndex(bi, xyz);
        int x = xyz[0], y = xyz[1], z = xyz[2];
        // 6-neighborhood: if any neighbor empty or OOB ⇒ boundary
        return checkAllDirections(x, y, z);
    }

    public boolean checkAllDirections(int xi, int yi, int zi) {
        boolean b = false;
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                for (int z = -1; z < 2; z++) {
                    b |= binEmpty(computeLinearBinIndex(xi + x, yi + y, zi + z));
                }
            }
        }
        return b;
    }

    /**
     * Fills DC cell vertex + active-cell buffers from this grid.
     * Only surface bins (per isSurfaceBin) and surface elements are used.
     * <p>
     * Call this once per DC rebuild, then feed into DCStitcher.buildSparse.
     */
    public <E extends ForcePhysicsElement> void fillDCBuffers(
            DCStitcher.DCMesh mesh,                       // reuse if you want, or ignore
            ForceGrid.NormalProvider<E> normalProvider,
            Vertex[] outCellVertices,                     // length = totalBins
            IntArrayList outActiveCells                   // cleared & refilled
    ) {
        if (outCellVertices.length != totalBins) {
            throw new IllegalArgumentException("outCellVertices must have length = totalBins");
        }

        Arrays.fill(outCellVertices, null);
        outActiveCells.clear();
        mesh.vertices.clear();
        mesh.quads.clear();
        mesh.tris.clear();

        // Iterate only used bins, then filter to actual surface bins
        for (int i = 0; i < usedBinCount; i++) {
            int bi = usedBins[i];
            if (!isSurfaceBin(bi)) {
                continue;
            }

            T elem = surfaceElementInBin(bi);
            if (elem == null) {
                continue;
            }

            Vec3 wp = elem.position();
            @SuppressWarnings("unchecked")
            E e = (E) elem;

            Vector3f normal = normalProvider.normalFor(e);
            if (normal == null || normal.lengthSquared() == 0f) {
                normal = new Vector3f(0, 1, 0); // fallback
            } else {
                normal.normalize();
            }

            // Bin index == DC cell index
            outCellVertices[bi] = new Vertex(
                    new Vector3f((float) wp.x, (float) wp.y, (float) wp.z),
                    normal
            );
            outActiveCells.add(bi);
        }
    }

    /**
     * Convenience one-shot DC mesh builder.
     * This allocates buffers; for real use, hoist Vertex[] / IntArrayList / DCMesh
     * into the ForceCloud/ForceGrid and reuse them.
     */
    public <E extends ForcePhysicsElement> DCStitcher.DCMesh buildDCMesh(
            ForceGrid.NormalProvider<E> normalProvider,
            boolean emitTrianglesIfThree
    ) {
        DCStitcher.DCMesh mesh = new DCStitcher.DCMesh();
        Vertex[] cellVertices = new Vertex[totalBins];
        IntArrayList activeCells = new IntArrayList(usedBinCount);

        fillDCBuffers(mesh, normalProvider, cellVertices, activeCells);

        // Now stitch
        return DCStitcher.buildSparse(
                binCountX,
                binCountY,
                binCountZ,
                activeCells,
                cellVertices,
                emitTrianglesIfThree
        );
    }

    public T surfaceElement(int cellX, int cellY, int cellZ) {
        int bi = computeLinearBinIndex(cellX, cellY, cellZ);
        T element = null;
        if (bi < 0) return element;

        List<T> bin = bins[bi];
        if (bin == null || bin.isEmpty()) return element;

        bin.removeIf(el -> !(el instanceof AvatarEntity));
        for (T p : bin) {
            if (p.surface()) {
                element = p;
            }
        }
        return element;
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
                        Vec3 pp = p.position();
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
            Vec3 pp = p.position();
            double dxp = pp.x - pos.x;
            double dyp = pp.y - pos.y;
            double dzp = pp.z - pos.z;
            if (dxp * dxp + dyp * dyp + dzp * dzp <= r2) {
                result.add(p);
            }
        }
        return result;
    }

    public @Nullable T surfaceElementAtWorld(double x, double y, double z) {
        long[] c = new long[3];
        normalCoords(x, y, z, c); // world -> (cellX,cellY,cellZ) using origin + cellSize
        int bi = computeLinearBinIndex(c[0], c[1], c[2]);
        if (bi < 0) return null;

        List<T> bin = bins[bi];
        if (bin == null || bin.isEmpty()) return null;

        // pick the one marked as surface (your rebuildFrom sets PhysicsElement.surface(true))
        for (T p : bin) {
            if (p instanceof AvatarEntity pe && pe.surface()) return p;
        }
        return null;
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
            Vec3 pp = p.position();
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

    // Choose how to compute "direction" per point.
    public interface NormalProvider<T extends ForcePhysicsElement> {
        Vector3f normalFor(T element);
    }
}
