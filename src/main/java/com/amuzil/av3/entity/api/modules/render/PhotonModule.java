package com.amuzil.av3.entity.api.modules.render;

import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IRenderModule;
import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


public class PhotonModule implements IRenderModule {

    public static String id = PhotonModule.class.getSimpleName();

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {
    }

    @Override
    public void tick(AvatarEntity entity) {
        // For starting effects per tick
        if (entity.fxLocation() != null) {
            if (entity.oneShotFX()) {
                if (entity.tickCount <= 1) {
                    startEntityEffect(FXHelper.getFX(entity.fxLocation()), entity);
                }
            } else {
                startEntityEffect(FXHelper.getFX(entity.fxLocation()), entity);
            }
        }
    }

    @Override
    public void save(CompoundTag nbt) {
        nbt.putString("ID", id);
    }

    @Override
    public void load(CompoundTag nbt) {
        id = nbt.getString("ID");
    }

    public static void startEntityEffect(FX fx, Entity entity) {
        if (fx != null) {
            EntityEffectExecutor entityEffect = new EntityEffectExecutor(fx, entity.level(), entity, EntityEffectExecutor.AutoRotate.NONE);
            entityEffect.start();
        }
    }

//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void renderTick(AvatarEntity entity, float entityYaw, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight) {
//
//    }
}
