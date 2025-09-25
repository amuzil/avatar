package com.amuzil.omegasource.entity.modules.entity;

import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.api.IEntityModule;
import net.minecraft.nbt.CompoundTag;

public class TimeoutModule implements IEntityModule {
    @Override
    public String id() {
        return "timeout";
    }

    @Override
    public void init(AvatarEntity entity) {

    }

    @Override
    public void tick(AvatarEntity entity) {
        entity.tickDespawn();
    }

    @Override
    public void save(CompoundTag nbt) {

    }

    @Override
    public void load(CompoundTag nbt) {

    }
}
