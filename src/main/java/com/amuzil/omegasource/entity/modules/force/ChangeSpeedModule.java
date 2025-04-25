package com.amuzil.omegasource.entity.modules.force;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.SpeedTrait;
import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.modules.IForceModule;
import net.minecraft.nbt.CompoundTag;

public class ChangeSpeedModule implements IForceModule {

    String id = "change_speed";

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {

    }

    @Override
    public void tick(AvatarEntity entity) {
        SpeedTrait speedTrait = entity.getTrait("speed_factor", SpeedTrait.class);
        if (speedTrait == null) {
            Avatar.LOGGER.warn("No speed trait for a speed change module. Please add the module or remove the trait.");
            return;
        }
        float speed = (float) speedTrait.getSpeed();
        entity.setDeltaMovement(entity.getDeltaMovement().scale(speed));
    }

    @Override
    public void save(CompoundTag nbt) {
        nbt.putString("ID", id);
    }

    @Override
    public void load(CompoundTag nbt) {
        this.id = nbt.getString("ID");
    }
}
