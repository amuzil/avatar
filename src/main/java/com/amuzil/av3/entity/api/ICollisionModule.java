package com.amuzil.av3.entity.api;

import com.amuzil.av3.bending.form.BendingForm;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.construct.AvatarConstruct;
import com.amuzil.av3.entity.projectile.AvatarProjectile;
import net.minecraft.world.entity.Entity;

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
        void handle(AvatarProjectile entity, Entity hitEntity, BendingForm bendingForm, float size);
    }

    @FunctionalInterface
    interface ConstructHandler {
        void handle(AvatarConstruct entity, Entity hitEntity, float damage, float size);
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
