package com.amuzil.av3.events;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.input.InputModule;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import static com.amuzil.av3.data.attachment.AvatarAttachments.IS_BENDING;

@EventBusSubscriber(modid = Avatar.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    //    public static final ByteBufferBuilder DEBUG_BUILDER = new ByteBufferBuilder(256);
    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        Avatar.LOGGER.info("Setting up Avatar Mod client-side...");
        Avatar.INPUT_MODULE = new InputModule();
        boolean isBending = event.getPlayer().getData(IS_BENDING);
        if (isBending)
            Avatar.INPUT_MODULE.initiate();
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        if (Avatar.INPUT_MODULE != null) {
            Avatar.INPUT_MODULE.terminate();
            Avatar.INPUT_MODULE = null;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent e) {
//        if (e.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES || ClientUtil.isPaused()) return;
//        Minecraft mc = Minecraft.getInstance();
//        Level level = mc.level;
//        if (level == null) return;
//
//        MinecraftSpace space = MinecraftSpace.get(level);
//        ForceSystem fs = space.forceSystem();
//
//
//        PoseStack pose = e.getPoseStack();
//        pose.pushPose();
//
//        RenderSystem.disableCull();
////        final BufferBuilder builder = Tesselator.getInstance()
////                .begin(VertexFormat.Mode.TRIANGLES, VertexFormat.builder()
////                        .add("Position", VertexFormatElement.POSITION)
////                        .add("Color", VertexFormatElement.COLOR)
////                        .add("UV0", VertexFormatElement.UV0)
////                        .add("UV1", VertexFormatElement.UV1)
////                        .add("Normal", VertexFormatElement.NORMAL)
////                        .padding(1)
////                        .build());
//
//
//        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
//        VertexConsumer vc = buffers.getBuffer(RenderType.entityTranslucent(WHITE_TEX, true));
//
////        Tesselator.getInstance().begin(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
//        Vec3 cameraPos = Minecraft.getInstance().getCameraEntity().getEyePosition();
//        pose.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
//
//
//        int packedLight = LightTexture.FULL_BLOCK;
//
//
//        for (ForceCloud cloud : fs.clouds()) {
//            PoseStack.Pose last = pose.last();
//
//            if (cloud == null || cloud.isDead()) {
//                Thread.dumpStack();
//                continue;
//            }
//            ForceGrid<ForcePoint> grid = cloud.grid();
//            int nx = grid.binX();
//            int ny = grid.binY();
//            int nz = grid.binZ();
//
//            DCStitcher.CellVertexProvider provider =
//                    grid.toDCProvider((PhysicsElement pe) -> {
//                        Vec3 v = pe.vel(); // or your "direction"
//                        return new Vector3f((float) v.x, (float) v.y + 1, (float) v.z);
//                    });
//
//            DCStitcher.Mesh mesh = DCStitcher.build(nx, ny, nz, provider, false);
//            if (mesh.quads.isEmpty()) continue;
//
//
//            for (int[] quad : mesh.quads) {
//                Vertex v1 = mesh.vertices.get(quad[0]);
//                Vertex v2 = mesh.vertices.get(quad[1]);
//                Vertex v3 = mesh.vertices.get(quad[2]);
//                Vertex v4 = mesh.vertices.get(quad[3]);
//
//
//                Vector3f p1 = v1.position;
//                Vector3f p2 = v2.position;
//                Vector3f p3 = v3.position;
//                Vector3f p4 = v4.position;
//
//                Vector3f na = v1.normal;
//                Vector3f nb = v2.normal;
//                Vector3f nc = v3.normal;
//                Vector3f nd = v4.normal;
//
//                float[] uv0 = uvPlanar(p1, na, 2f);
//                float[] uv1 = uvPlanar(p2, nb, 2f);
//                float[] uv2 = uvPlanar(p3, nc, 2f);
//                float[] uv3 = uvPlanar(p4, nd, 2f);
//
//                vc.addVertex(last.pose(), p1.x, p1.y, p1.z)
//                        .setColor(255, 255, 255, 255)
//                        .setUv(uv0[0], uv0[1])
//                        .setOverlay(OverlayTexture.NO_OVERLAY)
//                        .setLight(packedLight)
//                        .setNormal(last, na.x, na.y, na.z);
//
//                vc.addVertex(last.pose(), p2.x, p2.y, p2.z)
//                        .setColor(255, 255, 255, 255)
//                        .setUv(uv1[0], uv1[1])
//                        .setOverlay(OverlayTexture.NO_OVERLAY)
//                        .setLight(packedLight)
//                        .setNormal(last, nb.x, nb.y, nb.z);
//
//                vc.addVertex(last.pose(), p3.x, p3.y, p3.z)
//                        .setColor(255, 255, 255, 255)
//                        .setUv(uv2[0], uv2[1])
//                        .setOverlay(OverlayTexture.NO_OVERLAY)
//                        .setLight(packedLight)
//                        .setNormal(last, nc.x, nc.y, nc.z);
//
//                vc.addVertex(last.pose(), p4.x, p4.y, p4.z)
//                        .setColor(255, 255, 255, 255)
//                        .setUv(uv3[0], uv3[1])
//                        .setOverlay(OverlayTexture.NO_OVERLAY)
//                        .setLight(packedLight)
//                        .setNormal(last, nd.x, nd.y, nd.z);
//            }
//        }
//        pose.popPose();
    }


//    private static void drawWireSphere(VertexConsumer vc, Matrix4f m, float cx, float cy, float cz,
//                                       float r, int slices, int stacks) {
//        // 3 great-circle loops (XY, XZ, YZ) for cheap visibility
//        circle(vc, m, cx, cy, cz, r, 1, 0, 0, 0, 1, 0); // XY
//        circle(vc, m, cx, cy, cz, r, 1, 0, 0, 0, 0, 1); // XZ
//        circle(vc, m, cx, cy, cz, r, 0, 1, 0, 0, 0, 1); // YZ
//        // optional finer stacks/slices
//        float dTheta = (float) (Math.PI / stacks);
//        for (int i = 1; i < stacks; i++) {
//            float theta = i * dTheta;
//            float yr = (float) Math.cos(theta) * r;
//            float rr = (float) Math.sin(theta) * r;
//            circle(vc, m, cx, cy + yr, cz, rr, 1, 0, 0, 0, 0, 1); // parallel
//        }
//    }
//
//    private static void circle(VertexConsumer vc, Matrix4f m, float cx, float cy, float cz, float r,
//                               float axx, float axy, float axz, float ayx, float ayy, float ayz) {
//        final int segs = 48;
//        float prevx = cx + r * axx, prevy = cy + r * axy, prevz = cz + r * axz;
//        for (int i = 1; i <= segs; i++) {
//            double t = (i * 2.0 * Math.PI) / segs;
//            float cos = (float) Math.cos(t), sin = (float) Math.sin(t);
//            float x = cx + r * (cos * axx + sin * ayx);
//            float y = cy + r * (cos * axy + sin * ayy);
//            float z = cz + r * (cos * axz + sin * ayz);
//            line(vc, m, prevx, prevy, prevz, x, y, z, 80, 200, 255, 255);
//            prevx = x;
//            prevy = y;
//            prevz = z;
//        }
//    }
//
//    private static void line(VertexConsumer vc, Matrix4f m,
//                             float x0, float y0, float z0, float x1, float y1, float z1,
//                             int r, int g, int b, int a) {
//        vc.addVertex(m, x0, y0, z0).setColor(r, g, b, a).setNormal(0, 1, 0);
//        vc.addVertex(m, x1, y1, z1).setColor(r, g, b, a).setNormal(0, 1, 0);
//    }
}
