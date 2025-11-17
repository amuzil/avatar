package com.amuzil.av3.events;

import com.amuzil.av3.Avatar;
import com.amuzil.carryon.physics.bullet.collision.space.MinecraftSpace;
import com.amuzil.carryon.physics.bullet.thread.util.ClientUtil;
import com.amuzil.magus.physics.core.ForceCloud;
import com.amuzil.magus.physics.core.ForcePoint;
import com.amuzil.magus.physics.core.ForceSystem;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

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
        if (e.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || ClientUtil.isPaused()) return;
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level; if (level == null) return;

        MinecraftSpace space = MinecraftSpace.get(level);
        ForceSystem fs = space.forceSystem();



        PoseStack pose = e.getPoseStack();
        pose.pushPose();

        PoseStack.Pose last = pose.last();
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();

//        MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(DEBUG_BUILDER);
//        var vc = immediate.getBuffer(RenderType.lines());

        VertexConsumer vc = buffers.getBuffer(RenderType.lines());



//        fs.clouds().clear();
        Vec3 relativePos;
        for (ForceCloud cloud : fs.clouds()) {

            if (cloud == null || cloud.isDead()) {
                Thread.dumpStack();
                continue;
            }
            relativePos = cloud.pos();
            int i = 0;
            for (ForcePoint p : cloud.points()) {
//                if (!p.surface())
//                    continue;
                Vec3 wp = p.pos();
//                System.out.println(wp);
                float x = (float) (wp.x - relativePos.x);
                float y = (float) (wp.y - relativePos.y);
                float z = (float) (wp.z - relativePos.z);
//                System.out.println("X: " + x + ", Y: " + y + ", Z: " + z);
                vc.addVertex(last.pose(), x, y, z).setColor(255,255,0,255)
                        .setNormal(last, 0,1,0);
                vc.addVertex(last.pose(), x, y + 0.05f, z).setColor(255,255,0,255)
                        .setNormal(last, 0,1,0);

                line(vc, last.pose(), x, y, z, x + 0.25f, y, z, 120, 200, 255, 255);
                line(vc, last.pose(), x, y, z, x, y + 0.25f, z, 120, 200, 255, 255);
                line(vc, last.pose(), x, y, z, x, y, z + 0.25f, 120, 200, 255, 255);
            }
//            System.out.println("Cloud tick: " + cloud.lifetime());

        }
        pose.popPose();
//       / RenderSystem.enableCull();
//        RenderSystem.enableDepthTest();
//        immediate.endBatch();

    }

    private static void drawWireSphere(VertexConsumer vc, Matrix4f m, float cx, float cy, float cz,
                                       float r, int slices, int stacks) {
        // 3 great-circle loops (XY, XZ, YZ) for cheap visibility
        circle(vc, m, cx, cy, cz, r, 1, 0, 0, 0, 1, 0); // XY
        circle(vc, m, cx, cy, cz, r, 1, 0, 0, 0, 0, 1); // XZ
        circle(vc, m, cx, cy, cz, r, 0, 1, 0, 0, 0, 1); // YZ
        // optional finer stacks/slices
        float dTheta = (float)(Math.PI / stacks);
        for (int i = 1; i < stacks; i++) {
            float theta = i * dTheta;
            float yr = (float)Math.cos(theta) * r;
            float rr = (float)Math.sin(theta) * r;
            circle(vc, m, cx, cy + yr, cz, rr, 1, 0, 0, 0, 0, 1); // parallel
        }
    }
    private static void circle(VertexConsumer vc, Matrix4f m, float cx, float cy, float cz, float r,
                               float axx, float axy, float axz, float ayx, float ayy, float ayz) {
        final int segs = 48;
        float prevx = cx + r * axx, prevy = cy + r * axy, prevz = cz + r * axz;
        for (int i = 1; i <= segs; i++) {
            double t = (i * 2.0 * Math.PI) / segs;
            float cos = (float)Math.cos(t), sin = (float)Math.sin(t);
            float x = cx + r * (cos * axx + sin * ayx);
            float y = cy + r * (cos * axy + sin * ayy);
            float z = cz + r * (cos * axz + sin * ayz);
            line(vc, m, prevx, prevy, prevz, x, y, z, 80, 200, 255, 255);
            prevx = x; prevy = y; prevz = z;
        }
    }
    private static void line(VertexConsumer vc, Matrix4f m,
                             float x0, float y0, float z0, float x1, float y1, float z1,
                             int r, int g, int b, int a) {
        vc.addVertex(m, x0, y0, z0).setColor(r, g, b, a).setNormal(0, 1, 0);
        vc.addVertex(m, x1, y1, z1).setColor(r, g, b, a).setNormal(0, 1, 0);
    }
}
