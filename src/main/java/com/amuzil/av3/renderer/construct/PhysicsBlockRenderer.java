package com.amuzil.av3.renderer.construct;

import com.amuzil.av3.entity.construct.AvatarRigidBlock;
import com.amuzil.caliber.physics.bullet.math.Convert;
import com.jme3.math.Quaternion;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Quaternionf;

import java.util.List;

public class PhysicsBlockRenderer extends EntityRenderer<AvatarRigidBlock> {
    // NOTE: This only renders block size 1.0
    private final BlockRenderDispatcher blockRenderer;
    private final BlockColors blockColors;

    public PhysicsBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
        this.blockColors = Minecraft.getInstance().getBlockColors();
    }

    @Override
    public void render(AvatarRigidBlock block, float yRot, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int packedLight) {
        BlockState state = block.getBlockState();
        if (state.getRenderShape() != RenderShape.MODEL) return;

        BlockPos blockPos = block.blockPosition();

        // Sample the biome tint once for this entity's position.
        // Only applied to quads where tintIndex >= 0 — untinted faces use white (1,1,1).
        int tintColor = blockColors.getColor(state, (BlockAndTintGetter) block.level(), blockPos, 0);
        float tr = (float)(tintColor >> 16 & 255) / 255.0F;
        float tg = (float)(tintColor >> 8  & 255) / 255.0F;
        float tb = (float)(tintColor        & 255) / 255.0F;

        var bakedModel = blockRenderer.getBlockModel(state);
        var random = RandomSource.create(42L);

        stack.pushPose();

        // --- 1) Apply rotation from physics ---
        Quaternionf rot = Convert.toMinecraft(block.getPhysicsRotation(new Quaternion(), partialTick));
        stack.mulPose(rot);

        // --- 2) Grid dimensions ---
        int sizeX = (int) block.width();
        int sizeY = (int) block.height();
        int sizeZ = (int) block.depth();

        // --- 3) Move origin so grid is centered on entity ---
        stack.translate(-(sizeX / 2.0), -(sizeY / 2.0), -(sizeZ / 2.0));

        // --- 4) Loop through each block in the AABB grid ---
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {

                    stack.pushPose();
                    stack.translate(x, y, z);
                    stack.translate(0.5, 0.5, 0.5);
                    stack.translate(-0.5, -0.5, -0.5);

                    PoseStack.Pose pose = stack.last();

                    // Render each render type the model uses
                    for (RenderType rt : bakedModel.getRenderTypes(state, random, ModelData.EMPTY)) {
                        // Map block render types to their entity-compatible equivalents.
                        // cutoutMipped covers most solid/cutout blocks (grass, dirt, stone, leaves).
                        // Fall back to cutoutMipped for any unrecognised type.
                        RenderType entityRt;
                        if (rt.equals(RenderType.translucent())) {
                            entityRt = RenderType.translucent();
                        } else if (rt.equals(RenderType.cutout())) {
                            entityRt = RenderType.cutout();
                        } else {
                            entityRt = RenderType.cutoutMipped();
                        }
                        VertexConsumer consumer = bufferSource.getBuffer(entityRt);

                        // Render all face directions + general (null) quads
                        for (Direction dir : Direction.values()) {
                            renderQuads(pose, consumer,
                                    bakedModel.getQuads(state, dir, random, ModelData.EMPTY, rt),
                                    packedLight, tr, tg, tb);
                        }
                        renderQuads(pose, consumer,
                                bakedModel.getQuads(state, null, random, ModelData.EMPTY, rt),
                                packedLight, tr, tg, tb);
                    }

                    stack.popPose();
                }
            }
        }

        stack.popPose();
    }

    /**
     * Renders a list of quads, applying the biome tint only to quads that have
     * a tintIndex >= 0. Untinted quads (dirt, stone faces, etc.) use white (1,1,1)
     * so their baked colors are preserved exactly.
     */
    private void renderQuads(PoseStack.Pose pose, VertexConsumer consumer, List<BakedQuad> quads,
                             int packedLight, float tr, float tg, float tb) {
        for (BakedQuad quad : quads) {
            float r, g, b;
            if (quad.getTintIndex() >= 0) {
                // Tinted quad (e.g. grass top overlay, foliage) — apply biome color
                r = tr;
                g = tg;
                b = tb;
            } else {
                // Untinted quad (e.g. dirt sides, stone) — white preserves baked colors
                r = 1.0F;
                g = 1.0F;
                b = 1.0F;
            }
            consumer.putBulkData(pose, quad, r, g, b, 1.0F, packedLight, OverlayTexture.NO_OVERLAY);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(AvatarRigidBlock block) {
        return null;
    }
}