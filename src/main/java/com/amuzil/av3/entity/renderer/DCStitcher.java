package com.amuzil.av3.entity.renderer;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SurfaceNets-style stitching for Dual Contouring vertices.
 * Inputs:
 * - dims: grid resolution (cells) along X,Y,Z
 * - dcPos: length==nx*ny*nz; per-cell QEF result or null if no vertex for that cell
 * - emitTrianglesIfThree: if a face has exactly 3 valid cells, emit one triangle
 * <p>
 * Output:
 * - positions: packed list of unique positions actually used
 * - indices: triangle index buffer (quads are split into two triangles with a stable diagonal)
 */
public final class DCStitcher {

    public static Mesh build(
            int nx, int ny, int nz,
            Vector3f[] dcPos, // length == nx*ny*nz ; null where absent
            boolean emitTrianglesIfThree
    ) {
        if (dcPos.length != nx * ny * nz) {
            throw new IllegalArgumentException("dcPos length mismatch");
        }

        Mesh mesh = new Mesh();

        // Map grid cell -> compacted vertex index (only for non-null dcPos).
        int[] gridToV = new int[dcPos.length];
        Arrays.fill(gridToV, -1);

        // Pack vertices (keep order stable for determinism).
        for (int z = 0, i = 0; z < nz; z++) {
            for (int y = 0; y < ny; y++) {
                for (int x = 0; x < nx; x++, i++) {
                    Vector3f p = dcPos[i];
                    if (p != null) {
                        gridToV[i] = mesh.positions.size();
                        mesh.positions.add(new Vector3f(p));
                    }
                }
            }
        }

        // Helpers
        final int sx = 1;
        final int sy = nx;
        final int sz = nx * ny;
        final int nx1 = nx - 1, ny1 = ny - 1, nz1 = nz - 1;

        // Single pass: emit faces in +X, +Y, +Z directions.
        for (int z = 0; z < nz; z++) {
            for (int y = 0; y < ny; y++) {
                int base = z * sz + y * sy; // x offset added by +sx
                for (int x = 0; x < nx; x++) {
                    int i000 = base + x * sx;
                    // +X face (YZ plane at x .. x+1) needs y < ny-1 && z < nz-1 && x < nx-1 (we use the lower-left layer)
                    if (x < nx1 && y < ny1 && z < nz1) {
                        // The four cells incident to this YZ face (all at the current x layer).
                        int c00 = i000;                // (x,   y,   z)
                        int c01 = i000 + sz;           // (x,   y,   z+1)
                        int c10 = i000 + sy;           // (x,   y+1, z)
                        int c11 = i000 + sy + sz;      // (x,   y+1, z+1)
                        emitFaceQuad(mesh, gridToV, c00, c01, c11, c10, emitTrianglesIfThree); // winding chosen for +X normal
                    }

                    // +Y face (XZ plane at y .. y+1) needs x < nx-1 && z < nz-1 && y < ny-1
                    if (x < nx1 && y < ny1 && z < nz1) {
                        int c00 = i000;                // (x,   y,   z)
                        int c10 = i000 + sx;           // (x+1, y,   z)
                        int c01 = i000 + sz;           // (x,   y,   z+1)
                        int c11 = i000 + sx + sz;      // (x+1, y,   z+1)
                        emitFaceQuad(mesh, gridToV, c00, c10, c11, c01, emitTrianglesIfThree); // winding for +Y normal
                    }

                    // +Z face (XY plane at z .. z+1) needs x < nx-1 && y < ny-1 && z < nz-1
                    if (x < nx1 && y < ny1 && z < nz1) {
                        int c00 = i000;                // (x,   y,   z)
                        int c10 = i000 + sx;           // (x+1, y,   z)
                        int c11 = i000 + sx + sy;      // (x+1, y+1, z)
                        int c01 = i000 + sy;           // (x,   y+1, z)
                        emitFaceQuad(mesh, gridToV, c00, c10, c11, c01, emitTrianglesIfThree); // winding for +Z normal
                    }
                }
            }
        }

        return mesh;
    }

    // Emits either two triangles (for a quad) or one triangle if exactly three corners exist,
    // otherwise emits nothing. Corner order must be consistent (counter-clockwise as seen
    // from +axis side of the face).
    private static void emitFaceQuad(
            Mesh mesh, int[] gridToV,
            int c0, int c1, int c2, int c3,
            boolean allowTriIfThree
    ) {
        int v0 = gridToV[c0], v1 = gridToV[c1], v2 = gridToV[c2], v3 = gridToV[c3];
        int valid = (v0 >= 0 ? 1 : 0) + (v1 >= 0 ? 1 : 0) + (v2 >= 0 ? 1 : 0) + (v3 >= 0 ? 1 : 0);

        if (valid == 4) {
            // Split quad along a stable diagonal to avoid T-junctions (choose the "shorter" diagonal if you like).
            // Here we use (v0,v2) consistently.
            mesh.triangles.add(new int[]{v0, v1, v2});
            mesh.triangles.add(new int[]{v0, v2, v3});
        } else if (allowTriIfThree && valid == 3) {
            // Find the missing corner and emit one triangle using the remaining cycle order to keep winding stable.
            if (v0 < 0) mesh.triangles.add(new int[]{v1, v2, v3});
            else if (v1 < 0) mesh.triangles.add(new int[]{v2, v3, v0});
            else if (v2 < 0) mesh.triangles.add(new int[]{v3, v0, v1});
            else /* v3 < 0 */ mesh.triangles.add(new int[]{v0, v1, v2});
        }
        // If 0,1,2 valid (when allowTriIfThree==false), we emit nothing to avoid cracks.
    }

    // Utility to compute linear index if you need it elsewhere.
    public static int idx(int x, int y, int z, int nx, int ny) {
        return (z * ny + y) * nx + x;
    }

    public static final class Mesh {
        public final List<Vector3f> positions = new ArrayList<>();
        public final List<int[]> triangles = new ArrayList<>(); // each int[3]
    }
}

