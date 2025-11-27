package com.amuzil.av3.renderer.mc;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SurfaceNets-style stitching for Dual Contouring vertices.
 * <p>
 * Inputs:
 * - dims: grid resolution (cells) along X,Y,Z
 * - provider: per-cell vertex (or null) for DC vertex
 * - emitTrianglesIfThree: if a face has exactly 3 valid cells, emit one triangle
 * <p>
 * Output:
 * - vertices: packed list of unique vertices actually used
 * - quads:    quad index buffer (faces with 4 valid corners)
 * - tris:     triangle index buffer (only when emitTrianglesIfThree && exactly 3 corners)
 */
public final class DCStitcher {

    public static DCMesh buildSparse(
            int nx, int ny, int nz,
            IntArrayList activeCells,
            Vertex[] cellVertices,
            boolean emitTrianglesIfThree
    ) {
        DCMesh mesh = new DCMesh();

        final int totalCells = nx * ny * nz;
        final int[] cellToV = new int[totalCells];
        Arrays.fill(cellToV, -1);

        // 1) Pack vertices only for active cells
        for (int i = 0, n = activeCells.size(); i < n; i++) {
            int cellIndex = activeCells.getInt(i);
            Vertex v = cellVertices[cellIndex];
            if (v == null) continue;

            int vertIndex = mesh.vertices.size();
            cellToV[cellIndex] = vertIndex;
            mesh.vertices.add(v);
        }

        final int strideX = 1;
        final int strideY = nx;
        final int strideZ = nx * ny;

        // 2) Emit faces: treat each active cell as the "min corner" for three faces
        for (int i = 0, n = activeCells.size(); i < n; i++) {
            int cellIndex = activeCells.getInt(i);

            int x = cellIndex % nx;
            int tmp = cellIndex / nx;
            int y = tmp % ny;
            int z = tmp / ny;

            // Face in X-Y plane (normal +Z): (x,y,z) – (x+1,y,z) – (x+1,y+1,z) – (x,y+1,z)
            if (x + 1 < nx && y + 1 < ny) {
                int c00 = cellIndex;
                int c10 = cellIndex + strideX;          // +X
                int c11 = c10 + strideY;                // +X,+Y
                int c01 = cellIndex + strideY;          // +Y
                emitFaceQuad(mesh, cellToV, c00, c10, c11, c01, emitTrianglesIfThree);
            }

            // Face in Y-Z plane (normal +X): (x,y,z) – (x,y,z+1) – (x,y+1,z+1) – (x,y+1,z)
            if (y + 1 < ny && z + 1 < nz) {
                int c00 = cellIndex;
                int c01 = cellIndex + strideZ;          // +Z
                int c11 = c01 + strideY;                // +Z,+Y
                int c10 = cellIndex + strideY;          // +Y
                emitFaceQuad(mesh, cellToV, c00, c01, c11, c10, emitTrianglesIfThree);
            }

            // Face in X-Z plane (normal +Y): (x,y,z) – (x+1,y,z) – (x+1,y,z+1) – (x,y,z+1)
            if (x + 1 < nx && z + 1 < nz) {
                int c00 = cellIndex;
                int c10 = cellIndex + strideX;          // +X
                int c11 = c10 + strideZ;                // +X,+Z
                int c01 = cellIndex + strideZ;          // +Z
                emitFaceQuad(mesh, cellToV, c00, c10, c11, c01, emitTrianglesIfThree);
            }
        }

        return mesh;
    }

    private static void emitFaceQuad(
            DCMesh mesh, int[] cellToV,
            int c0, int c1, int c2, int c3,
            boolean allowTriIfThree
    ) {
        int v0 = cellToV[c0];
        int v1 = cellToV[c1];
        int v2 = cellToV[c2];
        int v3 = cellToV[c3];

        int valid = (v0 >= 0 ? 1 : 0)
                + (v1 >= 0 ? 1 : 0)
                + (v2 >= 0 ? 1 : 0)
                + (v3 >= 0 ? 1 : 0);

        if (valid == 4) {
            addQuad(mesh, v0, v1, v2, v3);
        } else if (allowTriIfThree && valid == 3) {
            // Emit a triangle using the 3 valid corners
            if (v0 < 0) addTri(mesh, v1, v2, v3);
            else if (v1 < 0) addTri(mesh, v2, v3, v0);
            else if (v2 < 0) addTri(mesh, v3, v0, v1);
            else /* v3 < 0 */ addTri(mesh, v0, v1, v2);
        }
        // valid <= 2: emit nothing
    }

    public static int idx(int x, int y, int z, int nx, int ny) {
        return (z * ny + y) * nx + x;
    }

    private static void addQuad(DCMesh mesh, int a, int b, int c, int d) {
        mesh.quads.add(a);
        mesh.quads.add(b);
        mesh.quads.add(c);
        mesh.quads.add(d);
    }

    private static void addTri(DCMesh mesh, int a, int b, int c) {
        mesh.tris.add(a);
        mesh.tris.add(b);
        mesh.tris.add(c);
    }

    public static final class DCMesh {
        public final List<Vertex> vertices = new ArrayList<>();
        public final IntArrayList quads = new IntArrayList(); // 4*i..4*i+3
        public final IntArrayList tris = new IntArrayList(); // 3*i..3*i+2
    }
}