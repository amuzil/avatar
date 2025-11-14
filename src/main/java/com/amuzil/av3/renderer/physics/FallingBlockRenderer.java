package com.amuzil.av3.renderer.physics;

import com.amuzil.av3.entity.physics.RigidBlock;
import com.amuzil.caliber.physics.bullet.math.Convert;
import com.jme3.math.Quaternion;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Quaternionf;

public class FallingBlockRenderer extends EntityRenderer<RigidBlock> {
    private final BlockRenderDispatcher blockRenderer;

    public FallingBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(RigidBlock block, float yRot, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int packedLight) {
        BlockState state = block.getBlockState();
        if (state.getRenderShape() == RenderShape.MODEL) {
            stack.pushPose();
            Quaternionf rot = Convert.toMinecraft(block.getPhysicsRotation(new Quaternion(), partialTick));
            stack.mulPose(rot);
            stack.translate(-0.5D, -0.5D, -0.5D);
            this.blockRenderer.renderSingleBlock(state, stack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
            stack.popPose();
            super.render(block, yRot, partialTick, stack, bufferSource, packedLight);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(RigidBlock block) {
        return null;
    }
}
