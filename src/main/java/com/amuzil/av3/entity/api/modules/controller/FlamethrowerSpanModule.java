package com.amuzil.av3.entity.api.modules.controller;

import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IEntityModule;
import com.amuzil.av3.entity.construct.AvatarElementCollider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class FlamethrowerSpanModule implements IEntityModule {
    @Override
    public String id() {
        return "";
    }

    @Override
    public void init(AvatarEntity entity) {

        // Only tick if physics is enabled
        if (entity.physics() && entity.owner() != null) {
            Level level = entity.level();
            // max should be a skill trait
            int max = 5;
            for (int i = 0; i < max; i++) {
                AvatarElementCollider collider = new AvatarElementCollider(level);
            }
        }
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
