package com.amuzil.omegasource.entity.api;

import net.minecraft.world.phys.Vec3;

public interface IAvatarProjectile {

    void shoot(Vec3 location, Vec3 direction, double speed, double inAccuracy);

}
