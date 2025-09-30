package com.amuzil.omegasource.entity.modules.force;

import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.api.IForceModule;
import net.minecraft.nbt.CompoundTag;


public class BindModule implements IForceModule {

public static String id = BindModule.class.getSimpleName();

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {

    }

    @Override
    public void tick(AvatarEntity entity) {
        if (entity.owner() != null)
            entity.setPos(entity.owner().position());
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
