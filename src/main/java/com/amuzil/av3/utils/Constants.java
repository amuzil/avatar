package com.amuzil.av3.utils;

import com.amuzil.av3.Avatar;
import com.lowdragmc.photon.client.fx.FX;


public final class Constants {
    // This class is for storing constant values and strings

    public static final String MOD_ID = Avatar.MOD_ID;

    public static final String
            // Size
            SIZE = "size",
            MAX_SIZE = "max_size",
            HEIGHT_CURVE = "height_curve",
            WIDTH_CURVE = "width_curve",

            // Movement
            DASH_SPEED = "dash_speed",

            // Material Resources
            SOURCE_LEVEL = "source_level",
            SOURCE_CONSUMPTION = "source_consumption",

            // Defensive
            // Multiplier
            RESISTANCE = "resistance",
            THORNS_DAMAGE = "thorns_damage",
            // Flat reduction
            DAMAGE_REDUCTION = "damage_reduction",

            // Time
            LIFETIME = "lifetime",
            COMPONENT_LIFE = "component_life",
            FIRE_TIME = "fire_time",

            // Construct/shield properties/mobs
            HEALTH = "health",
            MAX_HEALTH = "max_health",

            // VFX
            RUNTIME = "runtime",
            MAX_RUNTIME = "max_runtime",
            FX = "fx",
            ONE_SHOT = "one_shot",
            ENTITY_ID = "entity_id",
            FIRE_COLOUR = "fire_colour",

            // Collision
            COLLISION_TYPE = "collision_type",
            KNOCKBACK_DIRECTION = "knockback_direction",
            KNOCKBACK = "knockback",
            DAMAGE = "damage",

            // Shoot properties
            ANGLE = "angle",
            RANDOMNESS = "randomness",
            RANGE = "range",
            SPEED_FACTOR = "speed_factor",
            SPEED = "speed",

            // Meta information
            BENDING_FORM = "bending_form",
            ELEMENT = "element",

            //  Move balancing/cost/resource management
            CHI_COST = "chi_cost",
            COOLDOWN = "cooldown",
            EXHAUSTION = "exhaustion";

    private Constants() {
        // Private constructor to prevent instantiation
    }
}
