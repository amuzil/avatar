package com.amuzil.av3.renderer.mobs;

import com.amuzil.av3.entity.mobs.SkyBisonEntity;
import com.amuzil.av3.entity.model.SkybisonModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SkyBisonRenderer extends GeoEntityRenderer<SkyBisonEntity> {
    public SkyBisonRenderer(EntityRendererProvider.Context context) {
        super(context, new SkybisonModel());
    }

    @Override
    public void render(SkyBisonEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SkyBisonEntity animatable) {
        return super.getTextureLocation(animatable);
    }
}