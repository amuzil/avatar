package com.amuzil.av3.api.carryon.api;

import com.amuzil.av3.api.carryon.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.av3.api.carryon.physics.bullet.collision.body.shape.MinecraftShape;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * Use this interface to create a physics entity.
 * 
 * @see PhysicsElement
 */
public interface EntityPhysicsElement extends PhysicsElement<Entity> {
    static boolean is(Entity entity) {
        return entity instanceof EntityPhysicsElement element && element.getRigidBody() != null;
    }

    static EntityPhysicsElement get(Entity entity) {
        return (EntityPhysicsElement) entity;
    }

    @Override
    @Nullable
    EntityRigidBody getRigidBody();

    @Override
    default MinecraftShape.Convex createShape() {
        return MinecraftShape.convex(this.cast().getBoundingBox());
    }

    default boolean skipVanillaEntityCollisions() {
        return false;
    }
}
