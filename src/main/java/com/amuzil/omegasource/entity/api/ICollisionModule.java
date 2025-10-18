package com.amuzil.omegasource.entity.api;

import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.projectile.AvatarProjectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;


public interface ICollisionModule extends IEntityModule {

    Map<Class<?>, CollisionHandler> COLLISION_HANDLERS = new HashMap<>();
    Map<Class<?>, ProjectileHandler> PROJECTILE_HANDLERS = new HashMap<>();
    Map<Class<?>, ConstructHandler> CONSTRUCT_HANDLERS = new HashMap<>();

    @FunctionalInterface
   interface CollisionHandler {
        void handle(AvatarEntity entity, Entity hitEntity);
    }

    @FunctionalInterface
    interface ProjectileHandler {
        void handle(AvatarProjectile entity, Entity hitEntity, float damage, float size);
    }

    @FunctionalInterface
    interface EffectHandler {
        void handle(AvatarProjectile entity, Entity hitEntity, Vec3 direction, float size);
    }

    @FunctionalInterface
    interface ConstructHandler {
        void handle(AvatarProjectile entity, Entity hitEntity);
    }

    static void registerCollisionHandler(Class<?> clazz, CollisionHandler handler) {
        COLLISION_HANDLERS.put(clazz, handler);
    }

    static void registerProjectileHandler(Class<?> clazz, ProjectileHandler handler) {
        PROJECTILE_HANDLERS.put(clazz, handler);
    }

    static void registerConstructHandler(Class<?> clazz, ConstructHandler handler) {
        CONSTRUCT_HANDLERS.put(clazz, handler);
    }
}
