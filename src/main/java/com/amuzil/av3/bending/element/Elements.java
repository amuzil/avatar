package com.amuzil.av3.bending.element;

import com.amuzil.av3.Avatar;
import com.amuzil.magus.skill.SkillCategory;
import com.amuzil.av3.bending.element.air.Airbending;
import com.amuzil.av3.bending.element.earth.Earthbending;
import com.amuzil.av3.bending.element.fire.Firebending;
import com.amuzil.av3.bending.element.water.Waterbending;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.HashMap;
import java.util.function.Supplier;


@EventBusSubscriber(modid = Avatar.MOD_ID)
public class Elements {
    public static final HashMap<String, Element> ALL_FOUR = new HashMap<>();

    public static DeferredRegister<SkillCategory> SKILL_CATEGORY_REGISTER = DeferredRegister.create(ResourceLocation.parse("skill_categories"), Avatar.MOD_ID);
    public static Registry<SkillCategory> SKILL_CATEGORIES = SKILL_CATEGORY_REGISTER.makeRegistry(RegistryBuilder::new);

    public static final Supplier<Element> AIRBENDING = SKILL_CATEGORY_REGISTER.register("airbending", Airbending::new);
    public static final Supplier<Element> WATERBENDING = SKILL_CATEGORY_REGISTER.register("waterbending", Waterbending::new);
    public static final Supplier<Element> EARTHBENDING = SKILL_CATEGORY_REGISTER.register("earthbending", Earthbending::new);
    public static final Supplier<Element> FIREBENDING = SKILL_CATEGORY_REGISTER.register("firebending", Firebending::new);

    public static Element AIR, WATER, EARTH, FIRE;

    public static Element get(ResourceLocation id) {
        return (Element) SKILL_CATEGORIES.get(id);
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            AIR = AIRBENDING.get();
            WATER = WATERBENDING.get();
            EARTH = EARTHBENDING.get();
            FIRE = FIREBENDING.get();
        });
    }
}
