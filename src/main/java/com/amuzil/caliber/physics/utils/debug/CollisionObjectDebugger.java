package com.amuzil.caliber.physics.utils.debug;

import com.amuzil.caliber.api.event.render.DebugRenderEvent;
import com.amuzil.caliber.physics.bullet.collision.body.ElementRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.MinecraftRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.shape.MinecraftShape;
import com.amuzil.caliber.physics.bullet.collision.space.MinecraftSpace;
import com.amuzil.caliber.physics.bullet.math.Convert;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
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

        // Start a batch of debug lines
        final BufferBuilder builder = Tesselator.getInstance()
                .begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        // Let external debug handlers draw if they want
        NeoForge.EVENT_BUS.post(new DebugRenderEvent(space, builder, stack, cameraPos, tickDelta));

        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        // Our terrain + rigid bodies
        space.getTerrainMap().values()
                .forEach(terrain -> CollisionObjectDebugger.renderBody(terrain, builder, stack, tickDelta, cameraPos));
        space.getRigidBodiesByClass(ElementRigidBody.class)
                .forEach(elementRigidBody -> CollisionObjectDebugger.renderBody(elementRigidBody, builder, stack, tickDelta, cameraPos));

        // Finalize and draw
        MeshData meshData = null;
        try {
            meshData = builder.build();
            if (meshData != null) {
                BufferUploader.drawWithShader(meshData);
            }
        } finally {
            if (meshData != null) {
                meshData.close(); // release native data
            }
        }
    }

    public static void renderBody(MinecraftRigidBody rigidBody,
                                  BufferBuilder builder,
                                  PoseStack stack,
                                  float tickDelta,
                                  net.minecraft.world.phys.Vec3 cameraPos) {
        final Vector3f position = rigidBody.isStatic()
                ? rigidBody.getPhysicsLocation(new Vector3f())
                : ((ElementRigidBody) rigidBody).getFrame().getLocation(new Vector3f(), tickDelta);

        final Quaternion rotation = rigidBody.isStatic()
                ? rigidBody.getPhysicsRotation(new Quaternion())
                : ((ElementRigidBody) rigidBody).getFrame().getRotation(new Quaternion(), tickDelta);

        renderShape(rigidBody.getMinecraftShape(), position, rotation,
                builder, stack, rigidBody.getOutlineColor(), 1.0f, cameraPos);
    }

    public static void renderShape(MinecraftShape shape,
                                   Vector3f position,
                                   Quaternion rotation,
                                   BufferBuilder builder,
                                   PoseStack stack,
                                   Vector3f color,
                                   float alpha,
                                   net.minecraft.world.phys.Vec3 cameraPos) {
        final var triangles = shape.getTriangles(Quaternion.IDENTITY);

        // Here we assume PoseStack is NOT already camera-relative,
        // so we convert world position into camera space ourselves.
        stack.pushPose();
        stack.translate(
                position.x - (float) cameraPos.x,
                position.y - (float) cameraPos.y,
                position.z - (float) cameraPos.z
        );
        stack.mulPose(Convert.toMinecraft(rotation));

        final var pose = stack.last().pose();

        for (var triangle : triangles) {
            final var vertices = triangle.getVertices();
            final var p1 = vertices[0];
            final var p2 = vertices[1];
            final var p3 = vertices[2];

            // Draw triangle edges as lines
            builder.addVertex(pose, p1.x, p1.y, p1.z).setColor(color.x, color.y, color.z, alpha);
            builder.addVertex(pose, p2.x, p2.y, p2.z).setColor(color.x, color.y, color.z, alpha);

            builder.addVertex(pose, p2.x, p2.y, p2.z).setColor(color.x, color.y, color.z, alpha);
            builder.addVertex(pose, p3.x, p3.y, p3.z).setColor(color.x, color.y, color.z, alpha);

            builder.addVertex(pose, p3.x, p3.y, p3.z).setColor(color.x, color.y, color.z, alpha);
            builder.addVertex(pose, p1.x, p1.y, p1.z).setColor(color.x, color.y, color.z, alpha);
        }

        stack.popPose();
    }
}
