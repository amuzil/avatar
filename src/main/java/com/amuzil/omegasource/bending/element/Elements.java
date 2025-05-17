package com.amuzil.omegasource.bending.element;

import com.amuzil.omegasource.api.magus.registry.Registries;
import com.amuzil.omegasource.bending.element.air.Airbending;
import com.amuzil.omegasource.bending.element.earth.Earthbending;
import com.amuzil.omegasource.bending.element.fire.Firebending;
import com.amuzil.omegasource.bending.element.water.Waterbending;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;


public class Elements {
    public static final HashMap<String, Element> ALL_FOUR = new HashMap<>();

    public static final Element AIR = new Airbending();
    public static final Element WATER = new Waterbending();
    public static final Element EARTH = new Earthbending();
    public static final Element FIRE = new Firebending();

    public static Element get(ResourceLocation id) {
        return (Element) Registries.SKILL_CATEGORIES.get().getValue(id);
    }

    public static void init() {
        Airbending.init();
        Waterbending.init();
        Earthbending.init();
        Firebending.init();
    }
}
