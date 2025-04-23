package com.amuzil.omegasource.entity.modules.collision;

import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.modules.ICollisionModule;
import net.minecraft.nbt.CompoundTag;

public class KnockbackModule implements ICollisionModule {

    @Override
    public String id() {
        return "knockback";
    }

    @Override
    public void init(AvatarEntity entity) {

    }

    @Override
    public void tick(AvatarEntity entity) {

    }

    @Override
    public void save(CompoundTag nbt) {

    }

    @Override
    public void load(CompoundTag nbt) {

    }
}
