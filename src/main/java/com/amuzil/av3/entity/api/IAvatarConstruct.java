package com.amuzil.av3.entity.api;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.phys.Vec3;


public interface IAvatarConstruct extends ItemSupplier, TraceableEntity {

    void shoot(Vec3 location, Vec3 direction, double speed, double inAccuracy);

    void control(float scale);

    Entity owner();

    void setOwner(Entity entity);

    boolean isControlled();

    void setControlled(boolean controlled);
}
