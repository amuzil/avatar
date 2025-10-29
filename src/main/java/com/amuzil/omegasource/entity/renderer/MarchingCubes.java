package com.amuzil.omegasource.entity.renderer;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.amuzil.omegasource.entity.renderer.MarchingCubesConstants.*;

/**
 * CPU translation of the provided compute shader "March" kernel.
 *
 * Notes:
 * - Corner order matches the shader's cubeCorners[] construction:
 *   0:(x,  y,  z)
 *   1:(x+1,y,  z)
 *   2:(x+1,y,  z+1)
 *   3:(x,  y,  z+1)
 *   4:(x,  y+1,z)
 *   5:(x+1,y+1,z)
 *   6:(x+1,y+1,z+1)
 *   7:(x,  y+1,z+1)
 *
 * - Triangles are built using triangulation[cubeIndex] and the cornerIndexA/BFromEdge
 *   maps, exactly like the shader. We interpolate *between the two cube corner positions*
 *   based on their densities, producing the 3 triangle vertices.
 *
 * - This method uses the world-space positions stored in field[x][y][z].pos.
 *   The cellSize parameter is not used by this translation (the shader doesn't use it either).
 */
public final class MarchingCubes {

    private MarchingCubes() {}

    // Shader-corner order (see header comment).
    private static final int[][] CORNER = new int[][]{
            {0,0,0}, // 0
            {1,0,0}, // 1
            {1,1,0}, // 2
            {0,1,0}, // 3
            {0,0,1}, // 4
            {1,0,1}, // 5
            {1,1,1}, // 6
            {0,1,1}  // 7
    };

    /**
     * Polygonize volume using the compute-shader logic.
     *
     * @param field scalar grid [sx][sy][sz]; each PointData must contain world-space pos and a density (w)
     * @param iso   isoLevel (same role as shader's isoLevel)
     * @param cellSize kept for API parity; not used here (positions come from field)
     */
    public static List<Triangle> polygonize(PointData[][][] field, float iso, float cellSize) {
        final int sx = field.length;
        final int sy = field[0].length;
        final int sz = field[0][0].length;

        final ArrayList<Triangle> out = new ArrayList<>(sx * sy * sz);

        // Loop over cubes (stop one before end along each axis, as in the shader)
        for (int x = 0; x < sx - 1; x++) {
            for (int y = 0; y < sy - 1; y++) {
                for (int z = 0; z < sz - 1; z++) {

                    // Fetch corners in the shader's order
                    final PointData[] c = new PointData[8];
                    for (int i = 0; i < 8; i++) {
                        int cx = x + CORNER[i][0];
                        int cy = y + CORNER[i][1];
                        int cz = z + CORNER[i][2];
                        c[i] = field[cx][cy][cz];
                    }

                    // Build cubeIndex (same test: "inside" when value < iso)
                    int cubeIndex = 0;
                    if (c[0].density < iso) cubeIndex |= 1;
                    if (c[1].density < iso) cubeIndex |= 2;
                    if (c[2].density < iso) cubeIndex |= 4;
                    if (c[3].density < iso) cubeIndex |= 8;
                    if (c[4].density < iso) cubeIndex |= 16;
                    if (c[5].density < iso) cubeIndex |= 32;
                    if (c[6].density < iso) cubeIndex |= 64;
                    if (c[7].density < iso) cubeIndex |= 128;

                    // Create triangles using triangulation table + edge->corner maps
                    final int[] triRow = TRI_TABLE[cubeIndex];
                    for (int i = 0; triRow[i] != -1; i += 3) {
                        int e0 = triRow[i];
                        int e1 = triRow[i + 1];
                        int e2 = triRow[i + 2];

                        Vector3f vA = interpolate(c[CORNER_A_FROM_EDGE[e0]], c[CORNER_B_FROM_EDGE[e0]], iso);
                        Vector3f vB = interpolate(c[CORNER_A_FROM_EDGE[e1]], c[CORNER_B_FROM_EDGE[e1]], iso);
                        Vector3f vC = interpolate(c[CORNER_A_FROM_EDGE[e2]], c[CORNER_B_FROM_EDGE[e2]], iso);

                        // Flat normal per face (like your current pipeline)
                        Vector3f n = faceNormal(vA, vB, vC);

                        Triangle t = new Triangle();
                        t.vertexA = new Vertex(vA, n);
                        t.vertexB = new Vertex(vB, n);
                        t.vertexC = new Vertex(vC, n);
                        out.add(t);
                    }
                }
            }
        }
        return out;
    }

    // === Helpers ===

    private static Vector3f interpolate(PointData a, PointData b, float iso) {
        float va = a.density;
        float vb = b.density;
        float denom = (vb - va);

        float t;
        if (Math.abs(denom) < 1e-6f) {
            t = 0.5f;
        } else {
            t = (iso - va) / denom;
            if (t < 0f) t = 0f; else if (t > 1f) t = 1f;
        }

        Vector3f pa = a.pos;
        Vector3f pb = b.pos;
        return new Vector3f(
                pa.x + (pb.x - pa.x) * t,
                pa.y + (pb.y - pa.y) * t,
                pa.z + (pb.z - pa.z) * t
        );
    }

    private static Vector3f faceNormal(Vector3f p0, Vector3f p1, Vector3f p2) {
        float ux = p1.x - p0.x, uy = p1.y - p0.y, uz = p1.z - p0.z;
        float vx = p2.x - p0.x, vy = p2.y - p0.y, vz = p2.z - p0.z;
        float nx = uy * vz - uz * vy;
        float ny = uz * vx - ux * vz;
        float nz = ux * vy - uy * vx;
        float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (len < 1e-9f) return new Vector3f(0, 1, 0);
        return new Vector3f(nx / len, ny / len, nz / len);
    }
}
