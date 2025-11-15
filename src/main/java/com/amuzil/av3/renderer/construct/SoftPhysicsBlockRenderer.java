package com.amuzil.av3.renderer.construct;

import com.amuzil.av3.entity.construct.AvatarRigidBlock;
import com.amuzil.av3.entity.construct.AvatarSoftBlock;
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

public class SoftPhysicsBlockRenderer extends EntityRenderer<AvatarSoftBlock> {
    private final BlockRenderDispatcher blockRenderer;

    public SoftPhysicsBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(AvatarSoftBlock block, float yRot, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int packedLight) {
        BlockState state = block.getBlockState();
        if (state.getRenderShape() == RenderShape.MODEL) {
            stack.pushPose();
            Quaternionf rot = Convert.toMinecraft(block.getPhysicsRotation(new Quaternion(), partialTick));
            stack.mulPose(rot);
            stack.translate(-0.5D, -0.5D, -0.5D);

            block.getPhysicsBody()
            this.entityRenderDispatcher.renderSingleBlock(state, stack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
            stack.popPose();
            super.render(block, yRot, partialTick, stack, bufferSource, packedLight);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(AvatarSoftBlock block) {
        return null;
    }
}
