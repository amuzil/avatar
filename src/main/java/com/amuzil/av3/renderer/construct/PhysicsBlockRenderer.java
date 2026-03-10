package com.amuzil.av3.renderer.construct;

import com.amuzil.av3.entity.construct.AvatarRigidBlock;
import com.amuzil.caliber.physics.bullet.math.Convert;
import com.jme3.math.Quaternion;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.LightTexture;
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

    // Gentler directional shading than vanilla (which uses 0.5 for DOWN).
    // Gives visual depth without making any face look too dark.
    private static float shade(Direction dir) {
        return switch (dir) {
            case DOWN  -> 0.75F;
            case UP    -> 1.00F;
            case NORTH, SOUTH -> 0.90F;
            case EAST,  WEST  -> 0.85F;
        };
    }

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
        BlockAndTintGetter level = (BlockAndTintGetter) block.level();

        // Sample world light at the block's current position
        int blockLight = block.level().getBrightness(net.minecraft.world.level.LightLayer.BLOCK, blockPos);
        int skyLight   = block.level().getBrightness(net.minecraft.world.level.LightLayer.SKY,   blockPos);
        int worldLight = LightTexture.pack(blockLight, skyLight);

        // Sample biome tint for this position
        int tintColor = blockColors.getColor(state, level, blockPos, 0);
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

                    for (RenderType rt : bakedModel.getRenderTypes(state, random, ModelData.EMPTY)) {
                        RenderType entityRt;
                        if (rt.equals(RenderType.translucent())) {
                            entityRt = RenderType.translucent();
                        } else if (rt.equals(RenderType.cutout())) {
                            entityRt = RenderType.cutout();
                        } else {
                            entityRt = RenderType.cutoutMipped();
                        }
                        VertexConsumer consumer = bufferSource.getBuffer(entityRt);

                        for (Direction dir : Direction.values()) {
                            List<BakedQuad> quads = bakedModel.getQuads(state, dir, random, ModelData.EMPTY, rt);
                            if (!quads.isEmpty()) {
                                renderQuads(pose, consumer, quads, worldLight, tr, tg, tb, shade(dir));
                            }
                        }
                        // Unculled quads (no associated face direction) — use top brightness
                        List<BakedQuad> unculled = bakedModel.getQuads(state, null, random, ModelData.EMPTY, rt);
                        if (!unculled.isEmpty()) {
                            renderQuads(pose, consumer, unculled, worldLight, tr, tg, tb, 1.0F);
                        }
                    }

                    stack.popPose();
                }
            }
        }

        stack.popPose();
    }

    /**
     * Renders quads with:
     * - Biome tint applied only to tinted quads (tintIndex >= 0)
     * - Gentle directional shade multiplier per face — no AO, no vanilla 0.5 bottom darkening
     * - World-sampled light level
     */
    private void renderQuads(PoseStack.Pose pose, VertexConsumer consumer, List<BakedQuad> quads,
                             int worldLight, float tr, float tg, float tb, float shade) {
        for (BakedQuad quad : quads) {
            float r, g, b;
            if (quad.isTinted()) {
                r = tr * shade;
                g = tg * shade;
                b = tb * shade;
            } else {
                r = shade;
                g = shade;
                b = shade;
            }
            consumer.putBulkData(pose, quad, r, g, b, 1.0F, worldLight, OverlayTexture.NO_OVERLAY);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(AvatarRigidBlock block) {
        return null;
    }
}