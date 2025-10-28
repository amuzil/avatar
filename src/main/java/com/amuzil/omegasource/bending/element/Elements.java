package com.amuzil.omegasource.bending.element;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.bending.element.air.Airbending;
import com.amuzil.omegasource.bending.element.earth.Earthbending;
import com.amuzil.omegasource.bending.element.fire.Firebending;
import com.amuzil.omegasource.bending.element.water.Waterbending;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.function.Supplier;


public class Elements {
    public static final HashMap<String, Element> ALL_FOUR = new HashMap<>();

    public static DeferredRegister<SkillCategory> SKILL_CATEGORY_REGISTER = DeferredRegister.create(ResourceLocation.parse("skill_categories"), Avatar.MOD_ID);
    public static Supplier<IForgeRegistry<SkillCategory>> SKILL_CATEGORIES = SKILL_CATEGORY_REGISTER.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<Element> AIR = SKILL_CATEGORY_REGISTER.register("airbending", Airbending::new);
    public static final RegistryObject<Element> WATER = SKILL_CATEGORY_REGISTER.register("waterbending", Waterbending::new);
    public static final RegistryObject<Element> EARTH = SKILL_CATEGORY_REGISTER.register("earthbending", Earthbending::new);
    public static final RegistryObject<Element> FIRE = SKILL_CATEGORY_REGISTER.register("firebending", Firebending::new);

    public static Element get(ResourceLocation id) {
        return (Element) SKILL_CATEGORIES.get().getValue(id);
    }

    public static void init() {
        Airbending.init();
        Waterbending.init();
        Earthbending.init();
        Firebending.init();
    }
}
