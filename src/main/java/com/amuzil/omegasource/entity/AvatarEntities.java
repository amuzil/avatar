package com.amuzil.omegasource.entity;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.entity.projectile.AvatarCurveProjectile;
import com.amuzil.omegasource.entity.projectile.AvatarDirectProjectile;
import com.amuzil.omegasource.entity.projectile.AvatarProjectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class AvatarEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Avatar.MOD_ID);

    public static final  RegistryObject<EntityType<AvatarProjectile>> AVATAR_PROJECTILE_ENTITY_TYPE =
            ENTITY_TYPES.register("avatar_projectile", () -> EntityType.Builder.<AvatarProjectile>of(AvatarProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).build("avatar_projectile"));

    public static final  RegistryObject<EntityType<AvatarDirectProjectile>> AVATAR_DIRECT_PROJECTILE_ENTITY_TYPE =
            ENTITY_TYPES.register("avatar_direct_projectile", () -> EntityType.Builder.<AvatarDirectProjectile>of(AvatarDirectProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).build("avatar_direct_projectile"));

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
