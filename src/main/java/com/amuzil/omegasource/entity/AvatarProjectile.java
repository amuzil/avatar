package com.amuzil.omegasource.entity;

import com.amuzil.omegasource.entity.modules.IForceModule;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AvatarProjectile extends AvatarEntity implements IAvatarProjectile {

    public AvatarProjectile(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        addForceModule((IForceModule) ModuleRegistry.create("Move"));
    }

    @Override
    public void shoot(Vec3 location, Vec3 direction, double speed, double inAccuracy) {

    }
}
