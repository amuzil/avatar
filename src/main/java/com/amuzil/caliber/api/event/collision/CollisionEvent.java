package com.amuzil.caliber.api.event.collision;

import com.amuzil.caliber.physics.bullet.collision.body.MinecraftRigidBody;
import net.neoforged.bus.api.Event;


public class CollisionEvent extends Event {
    private final Type type;
    private final MinecraftRigidBody main;
    private final MinecraftRigidBody other;
    private final float impulse;

    public CollisionEvent(Type type, MinecraftRigidBody main, MinecraftRigidBody other, float impulse) {
        this.type = type;
        this.main = main;
        this.other = other;
        this.impulse = impulse;
    }

    public Type getType() {
        return this.type;
    }

    public MinecraftRigidBody getMain() {
        return this.main;
    }

    public MinecraftRigidBody getOther() {
        return this.other;
    }

    public float getImpulse() {
        return this.impulse;
    }

    public enum Type {
        BLOCK,
        FLUID,
        ELEMENT;
    }
}
