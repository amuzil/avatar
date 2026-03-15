package com.amuzil.av3.entity;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.entity.construct.AvatarElementCollider;
import com.amuzil.av3.entity.construct.AvatarRigidBlock;
import com.amuzil.av3.entity.controller.AvatarPhysicsController;
import com.amuzil.av3.entity.mobs.SkyBisonEntity;
import com.amuzil.av3.entity.projectile.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class AvatarEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Avatar.MOD_ID);

    public static final Supplier<EntityType<AvatarProjectile>> PROJECTILE_ENTITY_TYPE =
            registerProjectile("projectile", AvatarProjectile::new, 0.5f, 0.5f);

    public static final Supplier<EntityType<AvatarWaterShield>> WATER_SHIELD_TYPE =
            registerProjectile("water_shield", AvatarWaterShield::new, 0.5f, 0.5f);

    public static final Supplier<EntityType<AvatarWaterRing>> WATER_RING_TYPE =
            registerProjectile("water_ring", AvatarWaterRing::new, 0.5f, 0.5f);

    public static final Supplier<EntityType<AvatarDirectProjectile>> DIRECT_PROJECTILE_ENTITY_TYPE =
            registerProjectile("direct_projectile", AvatarDirectProjectile::new, 0.5f, 0.5f);

    public static final Supplier<EntityType<AvatarCurveProjectile>> CURVE_PROJECTILE_ENTITY_TYPE =
            registerProjectile("curve_projectile", AvatarCurveProjectile::new, 0.5f, 0.5f);

    public static final Supplier<EntityType<AvatarBoundProjectile>> BOUND_PROJECTILE_ENTITY_TYPE =
            registerProjectile("bind_projectile", AvatarBoundProjectile::new, 0.5f, 0.5f);

    public static final Supplier<EntityType<AvatarOrbitProjectile>> ORBIT_PROJECTILE_ENTITY_TYPE =
            registerProjectile("orbit_projectile", AvatarOrbitProjectile::new, 0.5f, 0.5f);

    public static final Supplier<EntityType<AvatarWaterProjectile>> WATER_PROJECTILE_ENTITY_TYPE =
            registerProjectile("water_projectile", AvatarWaterProjectile::new, 0.5f, 0.5f);


    // Probably want to change the registration method later
    public static final Supplier<EntityType<AvatarPhysicsController>> PHYSICS_CONTROLLER_ENTITY_TYPE =
            registerPhysicsBody("physics_controller", AvatarPhysicsController::new, 0.5f, 0.5f, 10, 1);

    public static final Supplier<EntityType<AvatarElementCollider>> ELEMENT_COLLIDER_ENTITY_TYPE =
            registerPhysicsBody("element_collider", AvatarElementCollider::new, 0.5f, 0.5f, 20, 1);
    public static final Supplier<EntityType<AvatarRigidBlock>> RIGID_BLOCK_ENTITY_TYPE =
            registerPhysicsBody("rigid_block", AvatarRigidBlock::new, 1.0f, 1.0f, 10, 4);

    public static final Supplier<EntityType<SkyBisonEntity>> SKYBISON_ENTITY_TYPE =
            ENTITY_TYPES.register("skybison_entity", () -> EntityType.Builder.of(SkyBisonEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 0.5f).build("skybison_entity"));

    private static <T extends Entity> Supplier<EntityType<T>> registerPhysicsBody(
            String id, EntityType.EntityFactory<T> factory, float width, float height, int trackingRange, int updateInterval) {
        return ENTITY_TYPES.register(id, () ->
                EntityType.Builder.of(factory, MobCategory.MISC)
                        .sized(width, height).clientTrackingRange(trackingRange).updateInterval(updateInterval)
                        .build(id)
        );
    }

    private static <T extends Entity> Supplier<EntityType<T>> registerProjectile(
            String id, EntityType.EntityFactory<T> factory, float width, float height) {
        return ENTITY_TYPES.register(id, () ->
                EntityType.Builder.of(factory, MobCategory.MISC)
                        .sized(width, height).clientTrackingRange(64).updateInterval(1)
                        .build(id)
        );
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
