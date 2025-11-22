package com.amuzil.av3.entity.api.modules.force;

import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IAvatarConstruct;
import com.amuzil.av3.entity.api.IForceModule;
import net.minecraft.nbt.CompoundTag;


public class ControlModule implements IForceModule {

    public static String id = ControlModule.class.getSimpleName();

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {

    }

    @Override
    public void tick(AvatarEntity entity) {
        if (entity instanceof IAvatarConstruct construct) {
            if (construct.isControlled()) {
                construct.control(1.5f);
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
}
