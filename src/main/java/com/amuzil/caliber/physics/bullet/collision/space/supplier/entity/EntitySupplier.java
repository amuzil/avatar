package com.amuzil.caliber.physics.bullet.collision.space.supplier.entity;

import com.amuzil.caliber.api.EntityPhysicsElement;
import com.amuzil.caliber.physics.bullet.collision.body.ElementRigidBody;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;

import java.util.List;

public interface EntitySupplier {
    default List<Entity> getInsideOf(ElementRigidBody rigidBody, AABB box) {
        if (!rigidBody.isInWorld())
            return List.of();

        // Entity can be a Boat, Minecart, or any LivingEntity so long as it is not a
        // player in spectator mode.
        return rigidBody.getSpace().level().getEntitiesOfClass(Entity.class, box, entity -> (entity instanceof Boat || entity instanceof Minecart || (entity instanceof LivingEntity && !(entity instanceof Player player && this.getGameType(player) == GameType.SPECTATOR))) && !EntityPhysicsElement.is(entity));
    }

    GameType getGameType(Player player);
}
