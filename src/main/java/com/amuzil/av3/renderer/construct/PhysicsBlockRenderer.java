package com.amuzil.av3.renderer.construct;

import com.amuzil.av3.entity.construct.AvatarRigidBlock;
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

public class PhysicsBlockRenderer extends EntityRenderer<AvatarRigidBlock> {
    private final BlockRenderDispatcher blockRenderer;

    public PhysicsBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(AvatarRigidBlock block, float yRot, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int packedLight) {
        BlockState state = block.getBlockState();
        if (state.getRenderShape() != RenderShape.MODEL) return;

        stack.pushPose();

        // --- 1) Apply rotation from physics ---
        Quaternionf rot = Convert.toMinecraft(block.getPhysicsRotation(new Quaternion(), partialTick));
        stack.mulPose(rot);

        // --- 2) Grid dimensions ---
        int sizeX = (int) block.width();
        int sizeY = (int) block.height();
        int sizeZ = (int) block.depth();

        // --- 3) Move origin so grid is centered on entity ---
        // entity origin = center of whole structure
        stack.translate(-(sizeX / 2.0), -(sizeY / 2.0), -(sizeZ / 2.0));

        // --- 4) Loop through each block in the AABB grid ---
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {

                    stack.pushPose();
                    stack.translate(x, y, z);

                    // move block model to be centered in each cell
                    stack.translate(0.5, 0.5, 0.5);
                    stack.translate(-0.5, -0.5, -0.5);

                    blockRenderer.renderSingleBlock(
                            state, stack, bufferSource,
                            packedLight, OverlayTexture.NO_OVERLAY,
                            ModelData.EMPTY, null
                    );

                    stack.popPose();
                }
            }
        }

        stack.popPose();
    }


    @Override
    public ResourceLocation getTextureLocation(AvatarRigidBlock block) {
        return null;
    }
}
