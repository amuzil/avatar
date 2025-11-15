package com.amuzil.caliber.api;

import com.amuzil.caliber.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.PlayerRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.shape.MinecraftShape;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Use this interface to create a physics entity.
 * 
 * @see PhysicsElement
 */
public interface PlayerPhysicsElement extends PhysicsElement<Player> {
    static boolean is(Entity entity) {
        return entity instanceof PlayerPhysicsElement element && element.getRigidBody() != null;
    }

    static PlayerPhysicsElement get(Entity entity) {
        return (PlayerPhysicsElement) entity;
    }

    @Override
    @Nullable
    PlayerRigidBody getRigidBody();

    void setGroundContact(boolean onCollider, Vec3 supportVelocity);

    @Override
    default MinecraftShape.Convex createShape() {
        return MinecraftShape.convex(this.cast().getBoundingBox());
    }

    default boolean skipVanillaEntityCollisions() {
        return false;
    }
}
