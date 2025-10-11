package com.amuzil.omegasource.utils.sound;

import com.amuzil.omegasource.entity.AvatarEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;


public class AvatarEntitySound extends AbstractTickableSoundInstance {
    private final AvatarEntity entity;

    public AvatarEntitySound(AvatarEntity entity, SoundEvent sound) {
        super(sound, SoundSource.PLAYERS, entity.level().getRandom());
        this.entity = entity;
        this.looping = false;
        this.delay = 0;
        this.volume = 1.0f;
    }

    @Override
    public void tick() {
        if (!entity.isAlive()) {
            System.out.println("Stopping sound, entity is dead");
            this.stop();
        } else {
            this.x = entity.getX();
            this.y = entity.getY();
            this.z = entity.getZ();

            // Optional: manually fade volume by distance
            var player = Minecraft.getInstance().player;
            if (player != null) {
                double distance = player.distanceTo(entity);
                this.volume = (float) Math.max(0.0, 1.0 - (distance / 4.0)); // fade beyond 16 blocks
            }
        }
    }
}
