package com.amuzil.av3.renderer.mc;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.amuzil.av3.renderer.mc.MarchingCubesConstants.*;

public final class MarchingCubes {

    private MarchingCubes() {}

    // Shader corner order (same as before)
    private static final int[][] CORNER = new int[][]{
            {0,0,0},{1,0,0},{1,1,0},{0,1,0},
            {0,0,1},{1,0,1},{1,1,1},{0,1,1}
    };

    public static List<Triangle> polygonize(PointData[][][] field, float iso, float cellSize) {
        final int sx = field.length;
        final int sy = field[0].length;
        final int sz = field[0][0].length;
        final ArrayList<Triangle> out = new ArrayList<>(sx * sy * sz);

        for (int x = 0; x < sx - 1; x++) {
            for (int y = 0; y < sy - 1; y++) {
                for (int z = 0; z < sz - 1; z++) {

                    final PointData[] c = new PointData[8];
                    for (int i = 0; i < 8; i++) {
                        c[i] = field[x + CORNER[i][0]][y + CORNER[i][1]][z + CORNER[i][2]];
                    }

                    int cubeIndex = 0;
                    if (c[0].density > iso) cubeIndex |= 1;
                    if (c[1].density > iso) cubeIndex |= 2;
                    if (c[2].density > iso) cubeIndex |= 4;
                    if (c[3].density > iso) cubeIndex |= 8;
                    if (c[4].density > iso) cubeIndex |= 16;
                    if (c[5].density > iso) cubeIndex |= 32;
                    if (c[6].density > iso) cubeIndex |= 64;
                    if (c[7].density > iso) cubeIndex |= 128;

                    int[] triRow = TRI_TABLE[cubeIndex];
                    if (triRow[0] == -1) continue;

                    for (int i = 0; triRow[i] != -1; i += 3) {
                        int e0 = triRow[i];
                        int e1 = triRow[i + 1];
                        int e2 = triRow[i + 2];

                        Vector3f vA = interpolate(c[CORNER_A_FROM_EDGE[e0]], c[CORNER_B_FROM_EDGE[e0]], iso).mul(cellSize);
                        Vector3f vB = interpolate(c[CORNER_A_FROM_EDGE[e1]], c[CORNER_B_FROM_EDGE[e1]], iso).mul(cellSize);
                        Vector3f vC = interpolate(c[CORNER_A_FROM_EDGE[e2]], c[CORNER_B_FROM_EDGE[e2]], iso).mul(cellSize);


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

    private static Vector3f interpolate(PointData a, PointData b, float iso) {
        float va = a.density, vb = b.density;
        float denom = vb - va;
        float t = Math.abs(denom) < 1e-6f ? 0.5f : (iso - va) / denom;
        t = Math.max(0f, Math.min(1f, t));

        Vector3f pa = a.pos, pb = b.pos;
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
        float len = (float)Math.sqrt(nx*nx + ny*ny + nz*nz);
        if (len < 1e-9f) return new Vector3f(0,1,0);
        return new Vector3f(nx/len, ny/len, nz/len);
    }
}
