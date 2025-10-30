package com.amuzil.omegasource.entity.modules.entity;

import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.api.IEntityModule;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public class SoundModule implements IEntityModule {

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
        if (entity.level().isClientSide && entity.fxName() != null) {
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

    public static void startSoundEffect(String sfx, Entity entity) {
        if (sfx != null) {

        }
    }
}
