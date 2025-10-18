package com.amuzil.omegasource.utils.sound;

import com.amuzil.omegasource.entity.AvatarEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;


public class AvatarEntitySound extends AbstractTickableSoundInstance {
    private final AvatarEntity entity;
    private final int lifetime;
    private int tickCount = 0; // Entity's client-side tickCount doesn't sync

    public AvatarEntitySound(AvatarEntity entity, SoundEvent sound, int lifetime, boolean looping, float volume, int delay) {
        super(sound, SoundSource.PLAYERS, entity.level().getRandom());
        this.entity = entity;
        this.lifetime = lifetime;
        this.looping = looping;
        this.volume = volume;
        this.delay = delay;
    }

    public AvatarEntitySound(AvatarEntity entity, SoundEvent sound, int lifetime) {
        this(entity, sound, lifetime, true, 1.0f, 0);
    }

    @Override
    public void tick() {
        if (tickCount++ == lifetime) {
            this.stop();
        } else {
            this.x = entity.getX();
            this.y = entity.getY();
            this.z = entity.getZ();

            // Manually fade volume by distance
            var player = Minecraft.getInstance().player;
            if (player != null) {
                double distance = player.distanceTo(entity);
                this.volume = (float) Math.max(0.0, 1.0 - (distance / 12.0)); // fade beyond 16 blocks
            }
        }
    }
}
