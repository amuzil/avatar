package com.amuzil.av3.entity.renderer;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.IHasSDF;
import com.amuzil.av3.entity.projectile.AvatarProjectile;
import com.amuzil.av3.entity.renderer.sdf.SignedDistanceFunction;
import com.amuzil.carryon.physics.bullet.collision.space.MinecraftSpace;
import com.amuzil.magus.physics.core.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.*;

public class MarchingCubesEntityRenderer<T extends AvatarEntity> extends EntityRenderer<T> {

    //    private static final ResourceLocation WHITE_TEX = Avatar.id("textures/misc/white.png");
    public static final ResourceLocation WHITE_TEX = Avatar.id("textures/water.png");
    private static final int GRID_SIZE = 32;
    private static final float CELL_SIZE = 0.5f;
    private static final float ISOLEVEL = 0.0f;
    private static final long MESH_TTL_MS = 100L;
    final float TEX_SCALE = 2.0f; // e.g. 2 repeats per block
    private final Map<UUID, CachedMesh> meshCache = new HashMap<>();
    PointData[][][] voxels = new PointData[GRID_SIZE][GRID_SIZE][GRID_SIZE];
    Random random = new Random();

    public MarchingCubesEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    private static float fract(float x) {
        return x - (float) Math.floor(x);
    }

    /**
     * Return (u,v) for a vertex using dominant-axis planar mapping.
     * texScale: repeats per world unit (e.g. 1f means texture tiles every 1 block).
     */
    public static float[] uvPlanar(Vector3f p, Vector3f n, float texScale) {
        float ax = Math.abs(n.x), ay = Math.abs(n.y), az = Math.abs(n.z);
        float u, v;

        if (ax >= ay && ax >= az) {              // project to YZ plane
            u = p.z * texScale;
            v = p.y * texScale;
            if (n.x > 0f) u = -u;                // flip for consistent winding
        } else if (ay >= ax && ay >= az) {       // project to XZ
            u = p.x * texScale;
            v = p.z * texScale;
            if (n.y < 0f) u = -u;
        } else {                                 // project to XY
            u = p.x * texScale;
            v = p.y * texScale;
            if (n.z < 0f) u = -u;
        }

        // wrap to [0,1) to avoid huge UVs; small bias to reduce seams
        return new float[]{fract(u) + 1e-4f, fract(v) + 1e-4f};
    }

    private static float fbmNoise(float x, float y, float z, long seed) {
        // Small, smooth, subtle FBM: 3 octaves
        float freq = 1.2f;     // base frequency (world units -> noise space)
        float amp = 0.04f;    // base amplitude per octave
        float sum = 0f;
        float norm = 0f;

        for (int i = 0; i < 3; i++) {
            sum += amp * valueNoise3D(x * freq, y * freq, z * freq, seed + i * 1013L);
            norm += amp;
            freq *= 2f;
            amp *= 0.5f;
        }
        return (norm > 0f) ? (sum / norm) : 0f; // ~[-0.5..+0.5] after normalization
    }

    private static float valueNoise3D(float x, float y, float z, long seed) {
        int x0 = (int) Math.floor(x);
        int y0 = (int) Math.floor(y);
        int z0 = (int) Math.floor(z);
        int x1 = x0 + 1, y1 = y0 + 1, z1 = z0 + 1;

        float fx = x - x0, fy = y - y0, fz = z - z0;

        // Smooth fade (Perlin)
        float sx = fade(fx), sy = fade(fy), sz = fade(fz);

        float c000 = hash01(x0, y0, z0, seed);
        float c100 = hash01(x1, y0, z0, seed);
        float c010 = hash01(x0, y1, z0, seed);
        float c110 = hash01(x1, y1, z0, seed);
        float c001 = hash01(x0, y0, z1, seed);
        float c101 = hash01(x1, y0, z1, seed);
        float c011 = hash01(x0, y1, z1, seed);
        float c111 = hash01(x1, y1, z1, seed);

        float x00 = lerp(c000, c100, sx);
        float x10 = lerp(c010, c110, sx);
        float x01 = lerp(c001, c101, sx);
        float x11 = lerp(c011, c111, sx);

        float y0v = lerp(x00, x10, sy);
        float y1v = lerp(x01, x11, sy);

        float v = lerp(y0v, y1v, sz); // [0..1]
        return (v * 2f - 1f);         // map to [-1..1]
    }

    private static float fade(float t) {
        return t * t * (3f - 2f * t);
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    // Deterministic integer hash -> [0,1)
    private static float hash01(int x, int y, int z, long seed) {
        long h = seed;
        h ^= (x * 0x632BE59BD9B4E019L);
        h ^= (y * 0x9E3779B97F4A7C15L);
        h ^= (z * 0xC2B2AE3D27D4EB4FL);
        // final avalanche
        h ^= (h >>> 27);
        h *= 0x3C79AC492BA7B653L;
        h ^= (h >>> 33);
        // convert to [0,1)
        return ((h >>> 11) & 0x1FFFFF) / (float) (1 << 21);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick,
                       PoseStack pose, MultiBufferSource buffer, int packedLight) {
        pose.pushPose();
//
//        // Center the generated volume around the entity origin
////        float volumeSize = (GRID_SIZE - 1) * CELL_SIZE;
////        float half = volumeSize * 0.5f;
////        pose.translate(-half, -half, -half);
//
//        CachedMesh mesh = getOrBuildMesh(partialTick, entity);
//
        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(WHITE_TEX, true));
////        VertexConsumer vc = buffer.getBuffer(ShaderRegistry.getTriplanarRenderType(getTextureLocation(entity)));
        var last = pose.last();
//
//        for (int i = 0; i < mesh.triangles.size(); i++) {
//            Triangle tri = mesh.triangles.get(i);
//            Vector3f p0 = tri.vertexA.position;
//            Vector3f p1 = tri.vertexB.position;
//            Vector3f p2 = tri.vertexC.position;
//            Vector3f n = tri.vertexA.normal;
//
//            float[] uv0 = uvPlanar(p0, n, TEX_SCALE);
//            float[] uv1 = uvPlanar(p1, n, TEX_SCALE);
//            float[] uv2 = uvPlanar(p2, n, TEX_SCALE);
//
////            vc.vertex( p0.x, p0.y, p0.z)
//            vc.addVertex(last.pose(), p0.x, p0.y, p0.z)
//                    .setColor(255, 255, 255, 255).setUv(uv0[0], uv0[1])
//                    .setOverlay(OverlayTexture.NO_OVERLAY)
//                    .setLight(packedLight)
//                    .setNormal(last, n.x, n.y, n.z);
//
////            vc.vertex(p1.x, p1.y, p1.z)
//            vc.addVertex(last.pose(), p1.x, p1.y, p1.z)
//                    .setColor(255, 255, 255, 255).setUv(uv1[0], uv1[1])
//                    .setOverlay(OverlayTexture.NO_OVERLAY)
//                    .setLight(packedLight)
//                    .setNormal(last, n.x, n.y, n.z);
//
////            vc.vertex(p2.x, p2.y, p2.z)
//            vc.addVertex(last.pose(), p2.x, p2.y, p2.z)
//                    .setColor(255, 255, 255, 255).setUv(uv2[0], uv2[1])
//                    .setOverlay(OverlayTexture.NO_OVERLAY)
//                    .setLight(packedLight)
//                    .setNormal(last, n.x, n.y, n.z);
//
////             C again (degenerate 4th vertex so the QUADS mode groups correctly)
//            vc.addVertex(last.pose(), p2.x, p2.y, p2.z)
//                    .setColor(255, 255, 255, 255).setUv(uv2[0], uv2[1])
//                    .setOverlay(OverlayTexture.NO_OVERLAY)
//                    .setLight(packedLight)
//                    .setNormal(last, n.x, n.y, n.z);
//        }
//
//        pose.popPose();

//


//        fs.clouds().clear();
//        pose.pushPose();
//        System.out.println(fs.clouds());

        double ex = Mth.lerp(partialTick, entity.xOld, entity.getX());
        double ey = Mth.lerp(partialTick, entity.yOld, entity.getY());
        double ez = Mth.lerp(partialTick, entity.zOld, entity.getZ());

        var cam = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 camPos = cam.getPosition();
        double cx = camPos.x;
        double cy = camPos.y;
        double cz = camPos.z;

//        pose.translate(-cx, -cy, -cz);
        // 3. Undo the entity translation so pose is back in camera space
//        pose.translate(cx - ex, cy - ey, cz - ez);
//
//        int i = 0;
//        int j = 0;
////        fs.clouds().removeIf(ForceCloud::isDead);
////        fs.clouds().clear();
////        System.out.println("Remder tick.");
//
        if (!(entity instanceof AvatarProjectile)) {
            pose.popPose();
            return;
        }
        ForceCloud cloud = ((AvatarProjectile) entity).cloud;
        if (cloud == null) {

            pose.popPose();
            return;
        }

        last = pose.last();

        if (cloud.isDead()) {
            pose.popPose();
            return;
        }
        ForceGrid<ForcePoint> grid = cloud.grid();
        int nx = grid.binX();
        int ny = grid.binY();
        int nz = grid.binZ();

        DCStitcher.CellVertexProvider provider =
                grid.toDCProvider((PhysicsElement pe) -> {
                    Vec3 v = pe.vel(); // or your "direction"
                    return new Vector3f((float) v.x, (float) v.y + 1, (float) v.z);
                });

        DCStitcher.Mesh mesh = DCStitcher.build(nx, ny, nz, provider, true);
        if (mesh.quads.isEmpty()) {
            pose.popPose();
            return;
        }


        for (int[] quad : mesh.quads) {
            Vertex v1 = mesh.vertices.get(quad[0]);
            Vertex v2 = mesh.vertices.get(quad[1]);
            Vertex v3 = mesh.vertices.get(quad[2]);
            Vertex v4 = mesh.vertices.get(quad[3]);


            Vector3f p1 = v1.position;
            Vector3f p2 = v2.position;
            Vector3f p3 = v3.position;
            Vector3f p4 = v4.position;

            Vector3f na = v1.normal;
            Vector3f nb = v2.normal;
            Vector3f nc = v3.normal;
            Vector3f nd = v4.normal;

            float[] uv0 = uvPlanar(p1, na, 2f);
            float[] uv1 = uvPlanar(p2, nb, 2f);
            float[] uv2 = uvPlanar(p3, nc, 2f);
            float[] uv3 = uvPlanar(p4, nd, 2f);

            vc.addVertex(last.pose(), p1.x, p1.y, p1.z)
                    .setColor(255, 255, 255, 255)
                    .setUv(uv0[0], uv0[1])
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(packedLight)
                    .setNormal(last, na.x, na.y, na.z);

            vc.addVertex(last.pose(), p2.x, p2.y, p2.z)
                    .setColor(255, 255, 255, 255)
                    .setUv(uv1[0], uv1[1])
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(packedLight)
                    .setNormal(last, nb.x, nb.y, nb.z);

            vc.addVertex(last.pose(), p3.x, p3.y, p3.z)
                    .setColor(255, 255, 255, 255)
                    .setUv(uv2[0], uv2[1])
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(packedLight)
                    .setNormal(last, nc.x, nc.y, nc.z);

            vc.addVertex(last.pose(), p4.x, p4.y, p4.z)
                    .setColor(255, 255, 255, 255)
                    .setUv(uv3[0], uv3[1])
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(packedLight)
                    .setNormal(last, nd.x, nd.y, nd.z);
        }
        pose.popPose();

    }

    @Override
    public ResourceLocation getTextureLocation(AvatarEntity pEntity) {
        return WHITE_TEX;
    }

    public float nowSeconds(float partialTick, T entity) {
        // or use Minecraft.getInstance().level.getGameTime()
        long ticks = entity.level().getGameTime();
        return ticks / 20.0f + partialTick / 20.0f;
    }

    private CachedMesh getOrBuildMesh(float partialTicks, T entity) {
        long now = System.currentTimeMillis();
        UUID id = entity.getUUID();
        CachedMesh cached = meshCache.get(id);
        if (cached != null && (now - cached.builtAtMs) < MESH_TTL_MS) return cached;

        float time = nowSeconds(partialTicks, entity); // or pass partialTick from render()

        SignedDistanceFunction sdf = (entity instanceof IHasSDF has) ? has.rootSDF() : null;

        MinecraftSpace space = MinecraftSpace.get(entity.level());
        ForceSystem fs = space.forceSystem();
        ForceCloud cloud = null;
        if (fs != null && !fs.clouds().isEmpty())
            cloud = fs.clouds().get(0);

        double ex = Mth.lerp(partialTicks, entity.xOld, entity.getX());
        double ey = Mth.lerp(partialTicks, entity.yOld, entity.getY());
        double ez = Mth.lerp(partialTicks, entity.zOld, entity.getZ());

        float cx = (GRID_SIZE - 1) * CELL_SIZE * 0.5f;

        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                for (int z = 0; z < GRID_SIZE; z++) {
                    float wx = x * CELL_SIZE, wy = y * CELL_SIZE, wz = z * CELL_SIZE;

                    // centered entity-local sample
                    float dx = wx - cx, dy = wy - cx, dz = wz - cx;

                    // world-space sample position
                    double sx = ex + dx;
                    double sy = ey + dy;
                    double sz = ez + dz;

                    float d = -1f;
                    if (cloud != null) {
                        ForcePoint element = cloud.grid().surfaceElementAtWorld(sx, sy, sz);
                        if (element != null) d = 1f;
                    }

                    voxels[x][y][z] = new PointData(new Vector3f(dx, dy, dz), d, x, y, z);
                }
            }
        }
//        System.out.println("Surface Particles: " + j);

        List<Triangle> triangles = MarchingCubes.polygonize(voxels, ISOLEVEL, CELL_SIZE);
        CachedMesh out = new CachedMesh(triangles, now);
        meshCache.put(id, out);
        return out;
    }
}
