package com.amuzil.av3.api.carryon.physics.bullet.collision.body;

import com.amuzil.av3.api.carryon.physics.bullet.collision.body.shape.MinecraftShape;
import com.amuzil.av3.api.carryon.physics.bullet.collision.space.MinecraftSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;

public abstract class MinecraftRigidBody extends PhysicsRigidBody {
    protected final MinecraftSpace space;

    static {
        CollisionShape.setDefaultMargin(0.001f);
    }

    public MinecraftRigidBody(MinecraftSpace space, MinecraftShape shape, float mass) {
        super((CollisionShape) shape, mass);
        this.space = space;
    }

    public MinecraftRigidBody(MinecraftSpace space, MinecraftShape shape) {
        this(space, shape, massForStatic);
    }

    public MinecraftSpace getSpace() {
        return this.space;
    }

    public MinecraftShape getMinecraftShape() {
        return (MinecraftShape) super.getCollisionShape();
    }

    public abstract Vector3f getOutlineColor();
}