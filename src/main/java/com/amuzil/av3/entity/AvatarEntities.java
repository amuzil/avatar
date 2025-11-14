package com.amuzil.av3.entity;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.entity.physics.AvatarRigidBlock;
import com.amuzil.av3.entity.mobs.EntitySkybison;
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

    public static final Supplier<EntityType<AvatarProjectile>> AVATAR_PROJECTILE_ENTITY_TYPE =
            registerProjectile("avatar_projectile", AvatarProjectile::new, 0.5f, 0.5f);

    public static final Supplier<EntityType<AvatarDirectProjectile>> AVATAR_DIRECT_PROJECTILE_ENTITY_TYPE =
            registerProjectile("avatar_direct_projectile", AvatarDirectProjectile::new, 0.5f, 0.5f);

    public static final Supplier<EntityType<AvatarCurveProjectile>> AVATAR_CURVE_PROJECTILE_ENTITY_TYPE =
            registerProjectile("avatar_curve_projectile", AvatarCurveProjectile::new, 0.5f, 0.5f);

    public static final Supplier<EntityType<AvatarBoundProjectile>> AVATAR_BOUND_PROJECTILE_ENTITY_TYPE =
            registerProjectile("avatar_bind_projectile", AvatarBoundProjectile::new, 0.5f, 0.5f);

    public static final Supplier<EntityType<AvatarOrbitProjectile>> AVATAR_ORBIT_PROJECTILE_ENTITY_TYPE =
            registerProjectile("avatar_orbit_projectile", AvatarOrbitProjectile::new, 0.5f, 0.5f);

    public static final Supplier<EntityType<AvatarWaterProjectile>> AVATAR_WATER_PROJECTILE_ENTITY_TYPE =
            registerProjectile("avatar_water_projectile", AvatarWaterProjectile::new, 0.5f, 0.5f);

    public static final Supplier<EntityType<AvatarRigidBlock>> AVATAR_RIGID_BLOCK_ENTITY_TYPE =
            registerPhysicsBody("avatar_rigid_block", AvatarRigidBlock::new, 1.0f, 1.0f, 10, 4);

    public static final Supplier<EntityType<EntitySkybison>> AVATAR_SKYBISON_ENTITY_TYPE =
            ENTITY_TYPES.register("avatar_skybison_entity", () -> EntityType.Builder.<EntitySkybison>of(EntitySkybison::new, MobCategory.CREATURE)
                    .sized(0.5f, 0.5f).build("avatar_skybison_entity"));

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
                        .sized(width, height).clientTrackingRange(64).updateInterval(3)
                        .build(id)
        );
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
