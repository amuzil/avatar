package com.amuzil.caliber.api;

import com.amuzil.caliber.physics.bullet.collision.body.rigidbody.EntityRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.shape.MinecraftShape;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * Use this interface to create a physics entity.
 * 
 * @see PhysicsElement
 */
public interface EntityRigidPhysicsElement extends RigidPhysicsElement<Entity> {
    static boolean is(Entity entity) {
        return entity instanceof EntityRigidPhysicsElement element && element.getPhysicsBody() != null;
    }

    static EntityRigidPhysicsElement get(Entity entity) {
        return (EntityRigidPhysicsElement) entity;
    }

    @Override
    @Nullable
    EntityRigidBody getPhysicsBody();

    @Override
    default MinecraftShape.Convex createShape() {
        return MinecraftShape.convex(this.cast().getBoundingBox());
    }

    default boolean skipVanillaEntityCollisions() {
        return false;
    }
}
