package com.amuzil.av3.entity.api;

import com.amuzil.av3.entity.AvatarEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface IRenderModule extends IEntityModule {

    // This method gets called in the entity's render class.
//    @OnlyIn(Dist.CLIENT)
//    void renderTick(AvatarEntity entity, float entityYaw, float partialTick,
//                    PoseStack pose, MultiBufferSource buffer, int packedLight);
}
