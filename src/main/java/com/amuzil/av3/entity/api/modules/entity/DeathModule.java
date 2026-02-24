package com.amuzil.av3.entity.api.modules.entity;

import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IEntityModule;
import net.minecraft.nbt.CompoundTag;

public class DeathModule implements IEntityModule {
    @Override
    public String id() {
        return "";
    }

    @Override
    public void init(AvatarEntity entity) {

    }

    @Override
    public void tick(AvatarEntity entity) {

    }

    public void die(AvatarEntity entity) {

    }

    @Override
    public void save(CompoundTag nbt) {

    }

    @Override
    public void load(CompoundTag nbt) {

    }
}
