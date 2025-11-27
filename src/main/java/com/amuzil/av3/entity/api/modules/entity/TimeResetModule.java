package com.amuzil.av3.entity.api.modules.entity;

import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IEntityModule;
import com.amuzil.av3.entity.construct.AvatarElementCollider;
import net.minecraft.nbt.CompoundTag;

public class TimeResetModule implements IEntityModule {

    public static String id = TimeoutModule.class.getSimpleName();

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {

    }

    @Override
    public void tick(AvatarEntity entity) {
        if (!(entity instanceof AvatarElementCollider collider && collider.spawner() != null))
            entity.tickDespawn();
        else {
            if (entity.tickCount >= entity.maxLifetime()) {
                // Reset
                if (entity instanceof AvatarElementCollider) {
                    if (!((AvatarElementCollider) entity).reset())
                        ((AvatarElementCollider) entity).reset(true);
                }
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

