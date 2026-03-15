package com.amuzil.av3.utils.entity.damagesource;

import com.amuzil.av3.Avatar;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

/**
 * Holds ResourceKeys for each elemental damage type.
 * Each key maps to a corresponding JSON file under:
 *   data/av3/damage_type/element_bending.json
 */
public final class BendingDamageTypes {

    // Prevent instantiation
    private BendingDamageTypes() {}

    public static final ResourceKey<DamageType> FIRE_BENDING = register("fire_bending");
    public static final ResourceKey<DamageType> WATER_BENDING = register("water_bending");
    public static final ResourceKey<DamageType> EARTH_BENDING = register("earth_bending");
    public static final ResourceKey<DamageType> AIR_BENDING = register("air_bending");

    private static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Avatar.id(name));
    }
}
