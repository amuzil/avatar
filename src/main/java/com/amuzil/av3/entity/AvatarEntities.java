package com.amuzil.av3.entity;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.entity.mobs.EntitySkybison;
import com.amuzil.av3.entity.projectile.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class AvatarEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Avatar.MOD_ID);

    public static final Supplier<EntityType<AvatarProjectile>> AVATAR_PROJECTILE_ENTITY_TYPE =
            ENTITY_TYPES.register("avatar_projectile", () -> EntityType.Builder.<AvatarProjectile>of(AvatarProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).build("avatar_projectile"));

    public static final Supplier<EntityType<AvatarDirectProjectile>> AVATAR_DIRECT_PROJECTILE_ENTITY_TYPE =
            ENTITY_TYPES.register("avatar_direct_projectile", () -> EntityType.Builder.<AvatarDirectProjectile>of(AvatarDirectProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).build("avatar_direct_projectile"));

    public static final Supplier<EntityType<AvatarCurveProjectile>> AVATAR_CURVE_PROJECTILE_ENTITY_TYPE =
            ENTITY_TYPES.register("avatar_curve_projectile", () -> EntityType.Builder.<AvatarCurveProjectile>of(AvatarCurveProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).build("avatar_curve_projectile"));

    public static final Supplier<EntityType<AvatarBoundProjectile>> AVATAR_BOUND_PROJECTILE_ENTITY_TYPE =
            ENTITY_TYPES.register("avatar_bind_projectile", () -> EntityType.Builder.<AvatarBoundProjectile>of(AvatarBoundProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).build("avatar_bind_projectile"));

    public static final Supplier<EntityType<AvatarOrbitProjectile>> AVATAR_ORBIT_PROJECTILE_ENTITY_TYPE =
            ENTITY_TYPES.register("avatar_orbit_projectile", () -> EntityType.Builder.<AvatarOrbitProjectile>of(AvatarOrbitProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).build("avatar_orbit_projectile"));

    public static final Supplier<EntityType<AvatarWaterProjectile>> AVATAR_WATER_PROJECTILE_ENTITY_TYPE =
            ENTITY_TYPES.register("avatar_water_entity", () -> EntityType.Builder.<AvatarWaterProjectile>of(AvatarWaterProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).build("avatar_water_projectile"));

    public static final Supplier<EntityType<EntitySkybison>> AVATAR_SKYBISON_ENTITY_TYPE =
            ENTITY_TYPES.register("avatar_skybison_entity", () -> EntityType.Builder.<EntitySkybison>of(EntitySkybison::new, MobCategory.CREATURE)
                    .sized(0.5f, 0.5f).build("avatar_skybison_entity"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
