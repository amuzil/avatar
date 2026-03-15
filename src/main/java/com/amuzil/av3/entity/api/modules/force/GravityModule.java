package com.amuzil.av3.entity.api.modules.force;

import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IForceModule;
import net.minecraft.nbt.CompoundTag;

public class GravityModule implements IForceModule {

    public static final String id = GravityModule.class.getSimpleName();
    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {

    }

    @Override
    public void tick(AvatarEntity entity) {
        entity.setDeltaMovement(entity.getDeltaMovement().x, entity.getDeltaMovement().y - 9.82 / 1000, entity.getDeltaMovement().z);
    }

    @Override
    public void save(CompoundTag nbt) {

    }

    @Override
    public void load(CompoundTag nbt) {

    }
}
