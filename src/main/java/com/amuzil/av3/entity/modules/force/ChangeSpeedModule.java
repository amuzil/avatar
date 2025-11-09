package com.amuzil.av3.entity.modules.force;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IForceModule;
import com.amuzil.av3.utils.Constants;
import com.amuzil.magus.skill.traits.skilltraits.SpeedTrait;
import net.minecraft.nbt.CompoundTag;


public class ChangeSpeedModule implements IForceModule {

    public static String id = ChangeSpeedModule.class.getSimpleName();

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {

    }

    @Override
    public void tick(AvatarEntity entity) {
        SpeedTrait speedTrait = entity.getTrait(Constants.SPEED_FACTOR, SpeedTrait.class);
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
        id = nbt.getString("ID");
    }
}
