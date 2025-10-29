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
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector3f;

import java.util.*;

public class MarchingCubesEntityRenderer<T extends AvatarEntity> extends EntityRenderer<T> {

    private final Map<UUID, CachedMesh> meshCache = new HashMap<>();
    private static final ResourceLocation WHITE_TEX =
            ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "textures/misc/white.png");

    private static final int GRID_SIZE = 16;
    private static final float CELL_SIZE = 0.25f;
    private static final float ISOLEVEL = 0.0f;
    private static final long MESH_TTL_MS = 250L;

    PointData[][][] voxels = new PointData[GRID_SIZE][GRID_SIZE][GRID_SIZE];

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

        for (int i = 0; i < mesh.triangles.size(); i++) {
            Triangle tri = mesh.triangles.get(i);
            Vector3f p0 = tri.vertexA.position;
            Vector3f p1 = tri.vertexB.position;
            Vector3f p2 = tri.vertexC.position;
            Vector3f n  = tri.vertexA.normal;

            vc.vertex(last.pose(), p0.x, p0.y, p0.z)
                    .color(0,0,255,255).uv(0,0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(last.normal(), n.x, n.y, n.z)
                    .endVertex();

            vc.vertex(last.pose(), p1.x, p1.y, p1.z)
                    .color(255,0,0,255).uv(0,0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(last.normal(), n.x, n.y, n.z)
                    .endVertex();

            vc.vertex(last.pose(), p2.x, p2.y, p2.z)
                    .color(0,255,0,255).uv(0,0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(last.normal(), n.x, n.y, n.z)
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

        float cx = (GRID_SIZE - 1) * CELL_SIZE * 0.5f;
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                for (int z = 0; z < GRID_SIZE; z++) {
                    float wx = x * CELL_SIZE, wy = y * CELL_SIZE, wz = z * CELL_SIZE;
                    float dx = wx - cx, dy = wy - cx, dz = wz - cx;
                    float r = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
                    float density = r - 1.35f; // <0 inside sphere
                    voxels[x][y][z] = new PointData(new Vector3f(wx, wy, wz), density, x, y, z);
                }
            }
        }

        List<Triangle> triangles = MarchingCubes.polygonize(voxels, ISOLEVEL, CELL_SIZE);
        CachedMesh out = new CachedMesh(triangles, now);
        meshCache.put(id, out);
        return out;
    }
}
