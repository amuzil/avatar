package com.amuzil.omegasource.entity;

import com.amuzil.omegasource.Avatar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class AvatarEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Avatar.MOD_ID);

//    public static final RegistryObject<EntityType<AirProjectile>> AIR_PROJECTILE_ENTITY_TYPE =
//            ENTITY_TYPES.register("air_projectile", () -> EntityType.Builder.<AirProjectile>of(AirProjectile::new, MobCategory.MISC)
//                    .sized(0.5f, 0.5f).build("air_projectile"));
//
//    public static final RegistryObject<EntityType<WaterProjectile>> WATER_PROJECTILE_ENTITY_TYPE =
//            ENTITY_TYPES.register("water_projectile", () -> EntityType.Builder.<WaterProjectile>of(WaterProjectile::new, MobCategory.MISC)
//                    .sized(0.5f, 0.5f).build("water_projectile"));
//
//    public static final RegistryObject<EntityType<EarthProjectile>> EARTH_PROJECTILE_ENTITY_TYPE =
//            ENTITY_TYPES.register("earth_projectile", () -> EntityType.Builder.<EarthProjectile>of(EarthProjectile::new, MobCategory.MISC)
//                    .sized(0.5f, 0.5f).build("earth_projectile"));
//
//    public static final RegistryObject<EntityType<FireProjectile>> FIRE_PROJECTILE_ENTITY_TYPE =
//            ENTITY_TYPES.register("fire_projectile", () -> EntityType.Builder.<FireProjectile>of(FireProjectile::new, MobCategory.MISC)
//                    .sized(0.5f, 0.5f).build("fire_projectile"));
//
//    public static final RegistryObject<EntityType<ElementCollision>> COLLISION_ENTITY_TYPE =
//            ENTITY_TYPES.register("element_collision", () -> EntityType.Builder.<ElementCollision>of(ElementCollision::new, MobCategory.MISC)
//                    .sized(0.5f, 0.5f).build("element_collision"));

    public static final  RegistryObject<EntityType<AvatarProjectile>> AVATAR_PROJECTILE_ENTITY_TYPE =
            ENTITY_TYPES.register("avatar_projectile", () -> EntityType.Builder.<AvatarProjectile>of(AvatarProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).build("avatar_projectile"));

    public static final RegistryObject<EntityType<AvatarCurveProjectile>> AVATAR_CURVE_PROJECTILE_ENTITY_TYPE =
            ENTITY_TYPES.register("avatar_curve_projectile", () -> EntityType.Builder.<AvatarCurveProjectile>of(AvatarCurveProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).build("avatar_curve_projectile"));

    public static final  RegistryObject<EntityType<AvatarBoundEntity>> AVATAR_BIND_ENTITY_TYPE =
            ENTITY_TYPES.register("avatar_bind", () -> EntityType.Builder.<AvatarBoundEntity>of(AvatarBoundEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).build("avatar_bind"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
