package com.amuzil.carryon.example.entity;

import com.amuzil.carryon.CarryOn;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CarryonEntities {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, CarryOn.MOD_ID);

    public static final Supplier<EntityType<PhysicsFallingBlock>> PHYSICS_FALLING_BLOCK = register("physics_falling_block",
            EntityType.Builder.of(PhysicsFallingBlock::new, MobCategory.MISC)
                    .clientTrackingRange(10)
                    .sized(1.0F, 1.0F).updateInterval(4));

    private static <T extends Entity> Supplier<EntityType<T>> register(String id, EntityType.Builder<T> builder) {
        return ENTITY_TYPES.register(id, () -> builder.build(CarryOn.id(id).toString()));
    }

    public static void register(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
    }
}
