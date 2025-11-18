package com.amuzil.av3.entity.renderer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SurfaceNets-style stitching for Dual Contouring vertices.
 *
 * Inputs:
 *  - dims: grid resolution (cells) along X,Y,Z
 *  - provider: per-cell vertex (or null) for DC vertex
 *  - emitTrianglesIfThree: if a face has exactly 3 valid cells, emit one triangle
 *
 * Output:
 *  - vertices: packed list of unique vertices actually used
 *  - quads:    quad index buffer (faces with 4 valid corners)
 *  - tris:     triangle index buffer (only when emitTrianglesIfThree && exactly 3 corners)
 */
public final class DCStitcher {

    public static Mesh build(
            int nx, int ny, int nz,
            CellVertexProvider provider,
            boolean emitTrianglesIfThree
    ) {
        Mesh mesh = new Mesh();

        int totalCells = nx * ny * nz;
        int[] gridToV = new int[totalCells];
        Arrays.fill(gridToV, -1);

        // Pack vertices in deterministic order, but ask the provider lazily.
        final int sx = 1;
        final int sy = nx;
        final int sz = nx * ny;

        for (int z = 0; z < nz; z++) {
            for (int y = 0; y < ny; y++) {
                int base = z * sz + y * sy;
                for (int x = 0; x < nx; x++) {
                    int i = base + x    * sx;
                    Vertex v = provider.cellVertex(x, y, z);
                    if (v != null) {
                        gridToV[i] = mesh.vertices.size();
                        mesh.vertices.add(v);
                    }
                }
            }
        }

        int nx1 = nx - 1, ny1 = ny - 1, nz1 = nz - 1;

        // Same face emission logic as before, just using gridToV.
        for (int z = 0; z < nz; z++) {
            for (int y = 0; y < ny; y++) {
                int base = z * sz + y * sy;
                for (int x = 0; x < nx; x++) {
                    int i000 = base + x * sx;

                    if (x < nx1 && y < ny1 && z < nz1) {
                        // +X
                        int c00 = i000;
                        int c01 = i000 + sz;
                        int c10 = i000 + sy;
                        int c11 = i000 + sy + sz;
                        emitFaceQuad(mesh, gridToV, c00, c01, c11, c10, emitTrianglesIfThree);
                    }

                    if (x < nx1 && y < ny1 && z < nz1) {
                        // +Y
                        int c00 = i000;
                        int c10 = i000 + sx;
                        int c01 = i000 + sz;
                        int c11 = i000 + sx + sz;
                        emitFaceQuad(mesh, gridToV, c00, c10, c11, c01, emitTrianglesIfThree);
                    }

                    if (x < nx1 && y < ny1 && z < nz1) {
                        // +Z
                        int c00 = i000;
                        int c10 = i000 + sx;
                        int c11 = i000 + sx + sy;
                        int c01 = i000 + sy;
                        emitFaceQuad(mesh, gridToV, c00, c10, c11, c01, emitTrianglesIfThree);
                    }
                }
            }
        }

        return mesh;
    }

    private static void emitFaceQuad(
            Mesh mesh, int[] gridToV,
            int c0, int c1, int c2, int c3,
            boolean allowTriIfThree
    ) {
        int v0 = gridToV[c0], v1 = gridToV[c1], v2 = gridToV[c2], v3 = gridToV[c3];
        int valid = (v0 >= 0 ? 1 : 0) + (v1 >= 0 ? 1 : 0) + (v2 >= 0 ? 1 : 0) + (v3 >= 0 ? 1 : 0);

        if (valid == 4) {
            // One proper quad
            mesh.quads.add(new int[]{v0, v1, v2, v3});
        } else if (allowTriIfThree && valid == 3) {
            // Optional: still emit triangle for the 3-corner case
            if (v0 < 0)      mesh.tris.add(new int[]{v1, v2, v3});
            else if (v1 < 0) mesh.tris.add(new int[]{v2, v3, v0});
            else if (v2 < 0) mesh.tris.add(new int[]{v3, v0, v1});
            else /* v3 < 0 */mesh.tris.add(new int[]{v0, v1, v2});
        }
        // valid <= 2: emit nothing
    }

    public static int idx(int x, int y, int z, int nx, int ny) {
        return (z * ny + y) * nx + x;
    }

    @FunctionalInterface
    public interface CellVertexProvider {
        // Return a per-cell vertex or null if no DC vertex for that cell.
        @Nullable
        Vertex cellVertex(int x, int y, int z);
    }

    public static final class Mesh {
        public final List<Vertex> vertices = new ArrayList<>();
        // primary: quads
        public final List<int[]> quads = new ArrayList<>(); // each int[4]
        // optional: triangles from 3-corner faces if you want to use them
        public final List<int[]> tris  = new ArrayList<>(); // each int[3]
    }
}