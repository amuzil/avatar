package com.amuzil.av3.entity.renderer.entity;

import com.amuzil.av3.entity.mobs.EntitySkybison;
import com.amuzil.av3.entity.model.SkybisonModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SkybisonRenderer extends GeoEntityRenderer<EntitySkybison> {
    public SkybisonRenderer(EntityRendererProvider.Context context) {
        super(context, new SkybisonModel());
    }

    @Override
    public void render(EntitySkybison entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(EntitySkybison animatable) {
        return super.getTextureLocation(animatable);
    }
}