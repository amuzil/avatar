package com.amuzil.av3.entity.api.modules.entity;

import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IEntityModule;
import com.amuzil.av3.entity.controller.AvatarPhysicsController;
import net.minecraft.nbt.CompoundTag;

public class DeathTimeModule implements IEntityModule {
    public static String id = DeathTimeModule.class.getSimpleName();

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {

    }

    @Override
    public void tick(AvatarEntity entity) {
        if (entity instanceof AvatarPhysicsController controller) {
            if (controller.dying())
                controller.deathTimer(controller.deathTimer() - 1);

            if (controller.deathTimer() == 0 && controller.dying())
                controller.kill();

        }
    }

    @Override
    public void save(CompoundTag nbt) {

    }

    @Override
    public void load(CompoundTag nbt) {

    }
}
