package com.amuzil.caliber.physics.bullet.collision.body.softbody;

import com.amuzil.caliber.physics.bullet.collision.body.shape.MinecraftShape;
import com.amuzil.caliber.physics.bullet.collision.space.MinecraftSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsSoftBody;
import com.jme3.math.Vector3f;

public abstract class MinecraftSoftBody extends PhysicsSoftBody {
    protected final MinecraftSpace space;

    static {
        CollisionShape.setDefaultMargin(0.001f);
    }

    public MinecraftSoftBody(MinecraftSpace space, MinecraftShape shape, float mass) {
        super();
        this.space = space;
    }

    public MinecraftSpace getSpace() {
        return this.space;
    }

    public MinecraftShape getMinecraftShape() {
        return (MinecraftShape) super.getCollisionShape();
    }

    public abstract Vector3f getOutlineColor();
}