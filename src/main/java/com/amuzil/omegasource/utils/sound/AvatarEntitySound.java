package com.amuzil.omegasource.utils.sound;

import com.amuzil.omegasource.entity.AvatarEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;


public class AvatarEntitySound extends AbstractTickableSoundInstance {
    private final AvatarEntity entity;

    public AvatarEntitySound(AvatarEntity entity, SoundEvent sound) {
        super(sound, SoundSource.PLAYERS, entity.level().getRandom());
        this.entity = entity;
        this.looping = false;
    }

    @Override
    public void tick() {
        if (!entity.isAlive()) {
            this.stop();
        } else {
            this.x = entity.getX();
            this.y = entity.getY();
            this.z = entity.getZ();
        }
    }
}
