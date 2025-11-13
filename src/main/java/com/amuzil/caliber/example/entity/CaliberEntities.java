package com.amuzil.caliber.example.entity;

import com.amuzil.caliber.CaliberPhysics;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CaliberEntities {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, CaliberPhysics.MOD_ID);

    public static final Supplier<EntityType<PhysicsFallingBlock>> PHYSICS_FALLING_BLOCK = register("physics_falling_block",
            EntityType.Builder.of(PhysicsFallingBlock::new, MobCategory.MISC)
                    .clientTrackingRange(10)
                    .sized(1.0F, 1.0F).updateInterval(4));

    private static <T extends Entity> Supplier<EntityType<T>> register(String id, EntityType.Builder<T> builder) {
        return ENTITY_TYPES.register(id, () -> builder.build(CaliberPhysics.id(id).toString()));
    }

    public static void register(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
    }
}
