package com.amuzil.magus.client.render;

import com.amuzil.caliber.physics.bullet.collision.space.MinecraftSpace;
import com.amuzil.magus.physics.core.ForceCloud;
import com.amuzil.magus.physics.core.ForcePoint;
import com.amuzil.magus.physics.core.ForceSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

// client-only
public class ForceDebugRenderer {

    public static void render(Level level,
                              PoseStack poseStack,
                              MultiBufferSource buffer,
                              float partialTick) {
        MinecraftSpace space = MinecraftSpace.get(level);
        if (space == null) return;

//        ForceSystem fs = space.forceSystem();
//        if (fs == null) return;
//
//        // Simple line renderer
//        VertexConsumer vc = buffer.getBuffer(RenderType.lines());
//
//        for (ForceCloud cloud : fs.clouds()) {
//            for (ForcePoint p : cloud.points()) {
//                Vec3 pos = p.pos();
//
//                // tiny vertical line to mark the point
//                double x = pos.x;
//                double y = pos.y;
//                double z = pos.z;
//
//                float r = 1.0f, g = 0.2f, b = 0.2f, a = 1.0f; // pick colors per type later
//
//                vc.addVertex(poseStack.last().pose(), (float) x, (float) y, (float) z)
//                        .setColor(r, g, b, a)
//                        .setNormal(0, 1, 0);
//
//                vc.addVertex(poseStack.last().pose(), (float) x, (float) (y + 0.2), (float) z)
//                        .setColor(r, g, b, a)
//                        .setNormal(0, 1, 0);
//            }
//        }
    }
}
