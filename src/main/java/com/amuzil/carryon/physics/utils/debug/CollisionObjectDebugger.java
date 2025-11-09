package com.amuzil.carryon.physics.utils.debug;

import com.amuzil.carryon.api.event.render.DebugRenderEvent;
import com.amuzil.carryon.physics.bullet.collision.body.ElementRigidBody;
import com.amuzil.carryon.physics.bullet.collision.body.MinecraftRigidBody;
import com.amuzil.carryon.physics.bullet.collision.body.shape.MinecraftShape;
import com.amuzil.carryon.physics.bullet.collision.space.MinecraftSpace;
import com.amuzil.carryon.physics.bullet.math.Convert;
import com.amuzil.magus.physics.core.ForceCloud;
import com.amuzil.magus.physics.core.ForcePoint;
import com.amuzil.magus.physics.core.ForceSystem;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;

/**
 * This class handles debug rendering on the client. Press F3+r to render all
 * {@link ElementRigidBody} objects present in the {@link MinecraftSpace}.
 */
public final class CollisionObjectDebugger {
    private static boolean enabled;

    private CollisionObjectDebugger() {}

    public static boolean toggle() {
        enabled = !enabled;
        return enabled;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void renderSpace(MinecraftSpace space, PoseStack stack, float tickDelta) {
        final var cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        final var builder = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        NeoForge.EVENT_BUS.post(new DebugRenderEvent(space, builder, stack, cameraPos, tickDelta));
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
//
//        space.getTerrainMap().values().forEach(terrain -> CollisionObjectDebugger.renderBody(terrain, builder, stack, tickDelta));
//        space.getRigidBodiesByClass(ElementRigidBody.class).forEach(elementRigidBody -> CollisionObjectDebugger.renderBody(elementRigidBody, builder, stack, tickDelta));

        renderForceClouds(space, builder, stack, cameraPos, tickDelta);
        builder.build();


    }

    public static void renderBody(MinecraftRigidBody rigidBody, BufferBuilder builder, PoseStack stack, float tickDelta) {
        final var position = rigidBody.isStatic() ? rigidBody.getPhysicsLocation(new Vector3f()) : ((ElementRigidBody) rigidBody).getFrame().getLocation(new Vector3f(), tickDelta);
        final var rotation = rigidBody.isStatic() ? rigidBody.getPhysicsRotation(new Quaternion()) : ((ElementRigidBody) rigidBody).getFrame().getRotation(new Quaternion(), tickDelta);
        renderShape(rigidBody.getMinecraftShape(), position, rotation, builder, stack, rigidBody.getOutlineColor(), 1.0f);
    }

    public static void renderForceClouds(MinecraftSpace space,
                                         BufferBuilder builder,
                                         PoseStack stack,
                                         Vec3 cameraPos,
                                         float tickDelta) {
        // If you wired ForceSystem into MinecraftSpace like: space.getForceSystem()
        ForceSystem fs = space.forceSystem();
        if (fs == null) return;

        for (ForceCloud cloud : fs.clouds()) {
            // Simple colour per cloud type; tweak as you like
            float r = 0.2f, g = 0.6f, b = 1.0f;
            // you can switch on cloud.type() to pick different colours per element

            for (ForcePoint p : cloud.points()) {
                Vec3 pos = p.pos();

                // position relative to camera
                double relX = pos.x - cameraPos.x;
                double relY = pos.y - cameraPos.y;
                double relZ = pos.z - cameraPos.z;

                stack.pushPose();
                stack.translate(relX, relY, relZ);

                // tiny vertical line to represent the point
                float len = 0.15f;

                builder.addVertex(stack.last().pose(), 0.0f, 0.0f, 0.0f)
                        .setColor(r, g, b, 1.0f);
                builder.addVertex(stack.last().pose(), 0.0f, len, 0.0f)
                        .setColor(r, g, b, 1.0f);

                stack.popPose();
            }
        }
    }

    public static void renderShape(MinecraftShape shape, Vector3f position, Quaternion rotation, BufferBuilder builder, PoseStack stack, Vector3f color, float alpha) {
        final var triangles = shape.getTriangles(Quaternion.IDENTITY);
        final var cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        for (var triangle : triangles) {
            final var vertices = triangle.getVertices();

            stack.pushPose();
            stack.translate(position.x - cameraPos.x, position.y - cameraPos.y, position.z - cameraPos.z);
            stack.mulPose(Convert.toMinecraft(rotation));
            final var p1 = vertices[0];
            final var p2 = vertices[1];
            final var p3 = vertices[2];

            builder.addVertex(stack.last().pose(), p1.x, p1.y, p1.z).setColor(color.x, color.y, color.z, alpha);
            builder.addVertex(stack.last().pose(), p2.x, p2.y, p2.z).setColor(color.x, color.y, color.z, alpha);
            builder.addVertex(stack.last().pose(), p3.x, p3.y, p3.z).setColor(color.x, color.y, color.z, alpha);
            builder.addVertex(stack.last().pose(), p1.x, p1.y, p1.z).setColor(color.x, color.y, color.z, alpha);
            stack.popPose();
        }
    }
}