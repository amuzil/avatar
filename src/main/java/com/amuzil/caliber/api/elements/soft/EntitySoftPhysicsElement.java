package com.amuzil.caliber.api.elements.soft;

import com.amuzil.caliber.api.elements.PhysicsElement;
import com.amuzil.caliber.physics.bullet.collision.body.shape.MinecraftShape;
import com.amuzil.caliber.physics.bullet.collision.body.softbody.EntitySoftBody;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * Use this interface to create a physics entity.
 * 
 * @see PhysicsElement
 */
public interface EntitySoftPhysicsElement extends SoftPhysicsElement<Entity> {
    static boolean is(Entity entity) {
        return entity instanceof EntitySoftPhysicsElement element && element.getPhysicsBody() != null;
    }

    static EntitySoftPhysicsElement get(Entity entity) {
        return (EntitySoftPhysicsElement) entity;
    }

    @Override
    @Nullable
    EntitySoftBody getPhysicsBody();

    @Override
    default MinecraftShape.Convex createShape() {
        return MinecraftShape.convex(this.cast().getBoundingBox());
    }

    default boolean skipVanillaEntityCollisions() {
        return false;
    }
}
