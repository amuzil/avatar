package com.amuzil.omegasource.entity.renderer;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.entity.AvatarEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.*;

import static com.amuzil.omegasource.entity.renderer.MarchingCubesConstants.*;

public class MarchingCubesEntityRenderer<T extends AvatarEntity> extends EntityRenderer<T> {

    private final Map<UUID, CachedMesh> meshCache = new HashMap<>();
    private static final ResourceLocation WHITE_TEX =
            ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "textures/misc/white.png");

    private static final int GRID_SIZE = 16;
    private static final float CELL_SIZE = 0.25f;
    private static final float ISOLEVEL = 0.0f;
    private static final long MESH_TTL_MS = 250L;

    public MarchingCubesEntityRenderer(EntityRendererProvider.Context ctx) { super(ctx); }

    @Override
    public void render(T entity, float entityYaw, float partialTick,
                       PoseStack pose, MultiBufferSource buffer, int packedLight) {
        pose.pushPose();

        // Center the generated volume around the entity origin
        float volumeSize = (GRID_SIZE - 1) * CELL_SIZE;
        float half = volumeSize * 0.5f;
        pose.translate(-half, -half, -half);

        CachedMesh mesh = getOrBuildMesh(entity);
        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(WHITE_TEX));
        var last = pose.last();

        for (int i = 0; i < mesh.positions.size(); i += 3) {
            Vec3 p0 = mesh.positions.get(i);
            Vec3 p1 = mesh.positions.get(i + 1);
            Vec3 p2 = mesh.positions.get(i + 2);
            Vec3 n  = mesh.normals.get(i); // flat normal per triangle (duplicated)

            vc.vertex(last.pose(), (float)p0.x, (float)p0.y, (float)p0.z)
                    .color(255,255,255,255).uv(0,0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(last.normal(), (float)n.x, (float)n.y, (float)n.z)
                    .endVertex();

            vc.vertex(last.pose(), (float)p1.x, (float)p1.y, (float)p1.z)
                    .color(255,255,255,255).uv(0,0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(last.normal(), (float)n.x, (float)n.y, (float)n.z)
                    .endVertex();

            vc.vertex(last.pose(), (float)p2.x, (float)p2.y, (float)p2.z)
                    .color(255,255,255,255).uv(0,0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(last.normal(), (float)n.x, (float)n.y, (float)n.z)
                    .endVertex();
        }

        pose.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(AvatarEntity pEntity) { return WHITE_TEX; }

    private CachedMesh getOrBuildMesh(T entity) {
        long now = System.currentTimeMillis();
        UUID id = entity.getUUID();
        CachedMesh cached = meshCache.get(id);
        if (cached != null && (now - cached.builtAtMs) < MESH_TTL_MS) return cached;

        // Density: negative inside sphere, positive outside (iso=0)
        PointData[][][] voxels = new PointData[GRID_SIZE][GRID_SIZE][GRID_SIZE];
        float cx = (GRID_SIZE - 1) * CELL_SIZE * 0.5f;
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                for (int z = 0; z < GRID_SIZE; z++) {
                    float wx = x * CELL_SIZE, wy = y * CELL_SIZE, wz = z * CELL_SIZE;
                    float dx = wx - cx, dy = wy - cx, dz = wz - cx;
                    float r = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
                    float density = r - 1.35f; // <0 inside sphere
                    voxels[x][y][z] = new PointData(new Vec3(wx, wy, wz), density, x, y, z);
                }
            }
        }

        List<Triangle> triangles = MarchingCubes.polygonize(voxels, ISOLEVEL, CELL_SIZE);
        MeshData mesh = buildMeshFromTriangles(triangles);
        CachedMesh out = new CachedMesh(mesh.vertices, mesh.normals, now);
        meshCache.put(id, out);
        return out;
    }

    public static MeshData buildMeshFromTriangles(List<Triangle> tris) {
        List<Vec3> positions = new ArrayList<>(tris.size() * 3);
        List<Vec3> normals   = new ArrayList<>(tris.size() * 3);
        for (Triangle t : tris) {
            positions.add(t.vertexA.position);
            positions.add(t.vertexB.position);
            positions.add(t.vertexC.position);

            normals.add(t.vertexA.normal);
            normals.add(t.vertexA.normal);
            normals.add(t.vertexA.normal);
        }
        return new MeshData(positions, normals);
    }

    private static final class CachedMesh {
        final List<Vec3> positions, normals; final long builtAtMs;
        CachedMesh(List<Vec3> p, List<Vec3> n, long t){ positions=p; normals=n; builtAtMs=t; }
    }

    private static final class Triangle { Vertex vertexA, vertexB, vertexC; }

    public static class Vertex {
        public Vec3 position, normal;
        public Vertex(Vec3 p, Vec3 n){ position=p; normal=n; }
    }

    public static final class MeshData {
        public final List<Vec3> vertices, normals;
        public MeshData(List<Vec3> v, List<Vec3> n){ vertices=v; normals=n; }
    }

    public static final class MarchingCubes {
        public static List<Triangle> polygonize(PointData[][][] field, float iso, float cellSize) {
            final int sx = field.length, sy = field[0].length, sz = field[0][0].length;
            final ArrayList<Triangle> out = new ArrayList<>(sx * sy * sz);
//            final Map<EdgeKey, Vertex> edgeCache = new HashMap<>();

            for (int x = 0; x < sx - 1; x++) {
                for (int y = 0; y < sy - 1; y++) {
                    for (int z = 0; z < sz - 1; z++) {

                        // Corner samples and absolute lattice coords
                        final PointData[] c = new PointData[8];
                        for (int i = 0; i < 8; i++) {
                            int cx = x + CORNER_OFFSETS[i][0];
                            int cy = y + CORNER_OFFSETS[i][1];
                            int cz = z + CORNER_OFFSETS[i][2];
                            c[i] = field[cx][cy][cz];
                        }

                        // Classic MC: set bit when value < iso (inside)
                        int cube = 0;
                        for (int i = 0; i < 8; i++) if (c[i].density < iso) cube |= (1 << i);

                        int mask = EDGE_TABLE[cube];
                        if (mask == 0) continue;

                        final Vertex[] eVerts = new Vertex[12];
                        for (int e = 0; e < 12; e++) {
                            if ((mask & (1 << e)) == 0) continue;

                            int a = EDGE_VERTICES[e][0];
                            int b = EDGE_VERTICES[e][1];

                            float va = c[a].density;
                            float vb = c[b].density;

// exact iso interpolation (mask guarantees vb != va in practice)
                            float t = (iso - va) / (vb - va);

                            Vec3 pa = c[a].pos;  // use the field's absolute positions
                            Vec3 pb = c[b].pos;

                            Vec3 p = new Vec3(
                                    pa.x + (pb.x - pa.x) * t,
                                    pa.y + (pb.y - pa.y) * t,
                                    pa.z + (pb.z - pa.z) * t
                            );;

                            Vertex v = new Vertex(p, new Vec3(0,1,0));
                            eVerts[e] = v;

                            double minX = x * cellSize, maxX = minX + cellSize;
                            double minY = y * cellSize, maxY = minY + cellSize;
                            double minZ = z * cellSize, maxZ = minZ + cellSize;
                            Vec3 pp = v.position;
                            assert (pp.x >= minX-1e-5 && pp.x <= maxX+1e-5
                                    && pp.y >= minY-1e-5 && pp.y <= maxY+1e-5
                                    && pp.z >= minZ-1e-5 && pp.z <= maxZ+1e-5) : "edge vertex escaped cell";

                        }


                        int[] tri = TRI_TABLE[cube];
                        for (int i = 0; i < tri.length; i += 3) {
                            int e0 = tri[i]; if (e0 == -1) break;
                            int e1 = tri[i + 1];
                            int e2 = tri[i + 2];
                            Vertex A = eVerts[e0], B = eVerts[e1], C = eVerts[e2];
                            if (A == null || B == null || C == null) continue;

                            Vec3 n = faceNormal(A.position, B.position, C.position); // keep winding
                            Triangle tTri = new Triangle();
                            tTri.vertexA = new Vertex(A.position, n);
                            tTri.vertexB = new Vertex(B.position, n);
                            tTri.vertexC = new Vertex(C.position, n);
                            out.add(tTri);
                        }
                    }
                }
            }
            return out;
        }

        private static Vec3 faceNormal(Vec3 p0, Vec3 p1, Vec3 p2) {
            double ux = p1.x - p0.x, uy = p1.y - p0.y, uz = p1.z - p0.z;
            double vx = p2.x - p0.x, vy = p2.y - p0.y, vz = p2.z - p0.z;
            double nx = uy * vz - uz * vy;
            double ny = uz * vx - ux * vz;
            double nz = ux * vy - uy * vx;
            double len = Math.sqrt(nx*nx + ny*ny + nz*nz);
            if (len < 1e-9) return new Vec3(0, 1, 0);
            return new Vec3(nx/len, ny/len, nz/len);
        }
    }
}
