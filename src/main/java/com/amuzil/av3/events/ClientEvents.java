package com.amuzil.av3.events;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.entity.renderer.DCStitcher;
import com.amuzil.carryon.physics.bullet.collision.space.MinecraftSpace;
import com.amuzil.carryon.physics.bullet.thread.util.ClientUtil;
import com.amuzil.magus.physics.core.ForceCloud;
import com.amuzil.magus.physics.core.ForcePoint;
import com.amuzil.magus.physics.core.ForceSystem;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

import static com.amuzil.av3.entity.renderer.MarchingCubesEntityRenderer.WHITE_TEX;
import static com.amuzil.av3.entity.renderer.MarchingCubesEntityRenderer.uvPlanar;

@EventBusSubscriber(modid = Avatar.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    //    public static final ByteBufferBuilder DEBUG_BUILDER = new ByteBufferBuilder(256);
    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        Avatar.inputModule.registerListeners();
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        if (Avatar.inputModule != null)
            Avatar.inputModule.terminate();
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent e) {
        if (e.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES || ClientUtil.isPaused()) return;
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) return;

        MinecraftSpace space = MinecraftSpace.get(level);
        ForceSystem fs = space.forceSystem();


        PoseStack pose = e.getPoseStack();
        pose.pushPose();

        RenderSystem.disableCull();
        final BufferBuilder builder = Tesselator.getInstance()
                .begin(VertexFormat.Mode.TRIANGLES, VertexFormat.builder()
                        .add("Position", VertexFormatElement.POSITION)
                        .add("Color", VertexFormatElement.COLOR)
                        .add("UV0", VertexFormatElement.UV0)
                        .add("UV1", VertexFormatElement.UV1)
                        .add("Normal", VertexFormatElement.NORMAL)
                        .padding(1)
                        .build());


        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vc = buffers.getBuffer(RenderType.entityTranslucent(WHITE_TEX, true));

//        Tesselator.getInstance().begin(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
        Vec3 cameraPos = Minecraft.getInstance().getCameraEntity().getEyePosition();
        pose.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        PoseStack.Pose last = pose.last();

        int packedLight = LightTexture.FULL_BLOCK;

        for (ForceCloud cloud : fs.clouds()) {

            if (cloud == null || cloud.isDead()) {
                Thread.dumpStack();
                continue;
            }

//            int i = 0;
//            for (ForcePoint p : cloud.points()) {
//                if (!p.surface())
//                    continue;
//                Vec3 wp = p.pos();
//                float x = (float) (wp.x);
//                float y = (float) (wp.y);
//                float z = (float) (wp.z);
////                System.out.println("X: " + x + ", Y: " + y + ", Z: " + z);
//                vc.addVertex(last.pose(), x, y, z).setColor(255, 255, 0, 255)
//                        .setNormal(last, 0, 1, 0);
//                vc.addVertex(last.pose(), x, y + 0.05f, z).setColor(255, 255, 0, 255)
//                        .setNormal(last, 0, 1, 0);
////
////                line(vc, last.pose(), x, y, z, x + 0.25f, y, z, 120, 200, 255, 255);
////                line(vc, last.pose(), x, y, z, x, y + 0.25f, z, 120, 200, 255, 255);
////                line(vc, last.pose(), x, y, z, x, y, z + 0.25f, 120, 200, 255, 255);
//            }
            DCStitcher.Mesh mesh = DCStitcher.build(cloud.grid().binX(), cloud.grid().binY(), cloud.grid().binZ(), cloud.grid().surfaceGridForDC(), false);
            if (mesh == null || mesh.triangles.isEmpty()) {
                continue;
            }

            // For now: wireframe the triangles so we don't fight vanilla solid pipeline yet.
            Vector3f edge1 = new Vector3f();
            Vector3f edge2 = new Vector3f();
            Vector3f n = new Vector3f();

            for (int[] tri : mesh.triangles) {
//                Vector3f a = mesh.vertices.get(tri[0]).position;
//                Vector3f b = mesh.vertices.get(tri[1]).position;
//                Vector3f c = mesh.vertices.get(tri[2]).position;
                Vector3f a = mesh.positions.get(tri[0]);
                Vector3f b = mesh.positions.get(tri[1]);
                Vector3f c = mesh.positions.get(tri[2]);
//                n = mesh.vertices.get(tri[3]).normal;
//                // Three edges per triangle
//                line(vc, last.pose(), a.x, a.y, a.z, b.x, b.y, b.z, 0, 255, 0, 255);
//                line(vc, last.pose(), b.x, b.y, b.z, c.x, c.y, c.z, 0, 255, 0, 255);
//                line(vc, last.pose(), c.x, c.y, c.z, a.x, a.y, a.z, 0, 255, 0, 255);
//                 n = normalize( (b - a) x (c - a) )
                edge1.set(b).sub(a);
                edge2.set(c).sub(a);
                n.set(edge2).cross(edge1);

                if (n.lengthSquared() == 0f) {
                    // Degenerate triangle, skip
                    continue;
                }
                n.normalize().mul(-1);
//                n = cloud.vel().toVector3f().mul(-1);

                float[] uv0 = uvPlanar(a, n, 2f);
                float[] uv1 = uvPlanar(b, n, 2f);
                float[] uv2 = uvPlanar(c, n, 2f);

                //            vc.vertex( p0.x, p0.y, p0.z)
                vc.addVertex(last.pose(), a.x, a.y, a.z)
                        .setColor(255, 255, 255, 255).setUv(uv0[0], uv0[1])
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(packedLight)
                        .setNormal(last, n.x, n.y, n.z);

//                System.out.println(a);
                //            vc.vertex(p1.x, p1.y, p1.z)
                vc.addVertex(last.pose(), b.x, b.y, b.z)
                        .setColor(255, 255, 255, 255).setUv(uv1[0], uv1[1])
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(packedLight)
                        .setNormal(last, n.x, n.y, n.z);

                //            vc.vertex(p2.x, p2.y, p2.z)
                vc.addVertex(last.pose(), c.x, c.y, c.z)
                        .setColor(255, 255, 255, 255).setUv(uv2[0], uv2[1])
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(packedLight)
                        .setNormal(last, n.x, n.y, n.z);

                vc.addVertex(last.pose(), c.x, c.y, c.z)
                        .setColor(255, 255, 255, 255).setUv(uv2[0], uv2[1])
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(packedLight)
                        .setNormal(last, n.x, n.y, n.z);
//            }
            }
//            System.out.println("Cloud tick: " + cloud.lifetime());
        }

        pose.popPose();
        MeshData meshData = null;
        try {
            meshData = builder.build();
            if (meshData != null) {
                RenderSystem.setShader(GameRenderer::getPositionColorShader);;
                BufferUploader.drawWithShader(meshData);
            }
        } finally {
            if (meshData != null) {
                meshData.close(); // release native data
            }
        }
        RenderSystem.enableCull();

//       / RenderSystem.enableCull();
//        RenderSystem.enableDepthTest();
//        immediate.endBatch();


    }

    private static DCStitcher.Mesh buildMeshForCloud(ForceCloud cloud) {
        List<ForcePoint> points = cloud.points();
        if (points.isEmpty()) return null;

        // 1) Compute AABB in world space
        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;

        for (ForcePoint p : points) {
            Vec3 wp = p.pos();
            double x = wp.x, y = wp.y, z = wp.z;
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
            if (z < minZ) minZ = z;
            if (z > maxZ) maxZ = z;
        }

        // Degenerate cloud
        if (minX >= maxX || minY >= maxY || minZ >= maxZ) {
            return null;
        }

        // 2) Choose a small grid resolution (tweak as needed)
        int nx = cloud.grid().binX(), ny = cloud.grid().binY(), nz = cloud.grid().binZ();

        float sizeX = (float) (maxX - minX);
        float sizeY = (float) (maxY - minY);
        float sizeZ = (float) (maxZ - minZ);

        float invDx = (nx > 1) ? (nx - 1) / sizeX : 0f;
        float invDy = (ny > 1) ? (ny - 1) / sizeY : 0f;
        float invDz = (nz > 1) ? (nz - 1) / sizeZ : 0f;

        Vector3f[] dcPos = new Vector3f[nx * ny * nz];

        // 3) Quantise each point into the grid; keep first point per cell
        for (ForcePoint p : points) {
            Vec3 wp = p.pos();

            int ix = Mth.clamp((int) ((wp.x - minX) * invDx), 0, nx - 1);
            int iy = Mth.clamp((int) ((wp.y - minY) * invDy), 0, ny - 1);
            int iz = Mth.clamp((int) ((wp.z - minZ) * invDz), 0, nz - 1);

            int idx = DCStitcher.idx(ix, iy, iz, nx, ny);
            if (dcPos[idx] == null) {
                // Store world-space position; DCStitcher doesn't care what space as long as indices are consistent
                dcPos[idx] = new Vector3f((float) wp.x, (float) wp.y, (float) wp.z);
            }
        }

        return DCStitcher.build(nx, ny, nz, dcPos, false);
    }

    private static void drawWireSphere(VertexConsumer vc, Matrix4f m, float cx, float cy, float cz,
                                       float r, int slices, int stacks) {
        // 3 great-circle loops (XY, XZ, YZ) for cheap visibility
        circle(vc, m, cx, cy, cz, r, 1, 0, 0, 0, 1, 0); // XY
        circle(vc, m, cx, cy, cz, r, 1, 0, 0, 0, 0, 1); // XZ
        circle(vc, m, cx, cy, cz, r, 0, 1, 0, 0, 0, 1); // YZ
        // optional finer stacks/slices
        float dTheta = (float) (Math.PI / stacks);
        for (int i = 1; i < stacks; i++) {
            float theta = i * dTheta;
            float yr = (float) Math.cos(theta) * r;
            float rr = (float) Math.sin(theta) * r;
            circle(vc, m, cx, cy + yr, cz, rr, 1, 0, 0, 0, 0, 1); // parallel
        }
    }

    private static void circle(VertexConsumer vc, Matrix4f m, float cx, float cy, float cz, float r,
                               float axx, float axy, float axz, float ayx, float ayy, float ayz) {
        final int segs = 48;
        float prevx = cx + r * axx, prevy = cy + r * axy, prevz = cz + r * axz;
        for (int i = 1; i <= segs; i++) {
            double t = (i * 2.0 * Math.PI) / segs;
            float cos = (float) Math.cos(t), sin = (float) Math.sin(t);
            float x = cx + r * (cos * axx + sin * ayx);
            float y = cy + r * (cos * axy + sin * ayy);
            float z = cz + r * (cos * axz + sin * ayz);
            line(vc, m, prevx, prevy, prevz, x, y, z, 80, 200, 255, 255);
            prevx = x;
            prevy = y;
            prevz = z;
        }
    }

    private static void line(VertexConsumer vc, Matrix4f m,
                             float x0, float y0, float z0, float x1, float y1, float z1,
                             int r, int g, int b, int a) {
        vc.addVertex(m, x0, y0, z0).setColor(r, g, b, a).setNormal(0, 1, 0);
        vc.addVertex(m, x1, y1, z1).setColor(r, g, b, a).setNormal(0, 1, 0);
    }
}
