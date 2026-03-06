package com.amuzil.av3.utils.entity.damagesource;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * Factory for creating elemental {@link DamageSource} instances.
 *
 * <p>Usage example:</p>
 * <pre>{@code
 *   // Direct damage (no attacker entity)
 *   entity.hurt(BendingDamageSources.fireBending(level), 5.0f);
 *
 *   // Damage with an attacker entity
 *   entity.hurt(BendingDamageSources.fireBending(level, attackerEntity), 5.0f);
 * }</pre>
 *
 * <p>Requires the corresponding damage_type JSON files to be present under
 * {@code data/av3/damage_type/}.</p>
 */
public final class BendingDamageSources {

    // Prevent instantiation
    private BendingDamageSources() {}

    // Air
    /** Air bending damage with no attacker. */
    public static DamageSource airBending(Level level) {
        return source(level, BendingDamageTypes.AIR_BENDING);
    }

    /** Air bending damage attributed to an attacker entity. */
    public static DamageSource airBending(Level level, Entity attacker) {
        return source(level, BendingDamageTypes.AIR_BENDING, attacker);
    }

    // Water
    /** Water bending damage with no attacker. */
    public static DamageSource waterBending(Level level) {
        return source(level, BendingDamageTypes.WATER_BENDING);
    }

    /** Water bending damage attributed to an attacker entity. */
    public static DamageSource waterBending(Level level, Entity attacker) {
        return source(level, BendingDamageTypes.WATER_BENDING, attacker);
    }

    // Earth
    /** Earth bending damage with no attacker. */
    public static DamageSource earthBending(Level level) {
        return source(level, BendingDamageTypes.EARTH_BENDING);
    }

    /** Earth bending damage attributed to an attacker entity. */
    public static DamageSource earthBending(Level level, Entity attacker) {
        return source(level, BendingDamageTypes.EARTH_BENDING, attacker);
    }

    // Fire
    /** Fire bending damage with no attacker. */
    public static DamageSource fireBending(Level level) {
        return source(level, BendingDamageTypes.FIRE_BENDING);
    }

    /** Fire bending damage attributed to an attacker entity. */
    public static DamageSource fireBending(Level level, Entity attacker) {
        return source(level, BendingDamageTypes.FIRE_BENDING, attacker);
    }


    private static DamageSource source(Level level, net.minecraft.resources.ResourceKey<DamageType> key) {
        Holder<DamageType> holder = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(key);
        return new DamageSource(holder);
    }

    private static DamageSource source(Level level, net.minecraft.resources.ResourceKey<DamageType> key, Entity attacker) {
        Holder<DamageType> holder = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(key);
        return new DamageSource(holder, attacker);
    }
}
