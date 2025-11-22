package com.amuzil.av3.entity.api.modules.client;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IFXModule;
import com.amuzil.av3.utils.sound.AvatarEntitySound;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SoundModule implements IFXModule {

    public static String id = SoundModule.class.getSimpleName();

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {
    }

    @Override
    public void tick(AvatarEntity entity) {
        if (entity.fxName() != null) {
            if (entity.oneShotFX()) {
                if (entity.tickCount <= 1) {
                    startSoundEffect(entity.fxName(), entity);
                }
            } else {
                startSoundEffect(entity.fxName(), entity);
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

    @OnlyIn(Dist.CLIENT)
    public static void startSoundEffect(String sfxName, AvatarEntity entity) {
        if (sfxName != null) {
            ResourceLocation id = Avatar.id(sfxName);
            SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(id);
            if (soundEvent != null)
                Minecraft.getInstance().getSoundManager()
                        .play(new AvatarEntitySound(entity, soundEvent, entity.maxLifetime(), false));
        }
    }
}
