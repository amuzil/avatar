package com.amuzil.av3.bending.element;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.element.air.Airbending;
import com.amuzil.av3.bending.element.earth.Earthbending;
import com.amuzil.av3.bending.element.fire.Firebending;
import com.amuzil.av3.bending.element.water.Waterbending;
import com.amuzil.magus.registry.Registries;
import com.amuzil.magus.skill.SkillCategory;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashMap;
import java.util.Random;


@EventBusSubscriber(modid = Avatar.MOD_ID)
public class Elements {
    public static final HashMap<String, Element> ALL_FOUR = new HashMap<>();

    private static final DeferredHolder<SkillCategory, Element> AIRBENDING = Registries.SKILL_CATEGORY_REGISTER.register("airbending", Airbending::new);
    private static final DeferredHolder<SkillCategory, Element> WATERBENDING = Registries.SKILL_CATEGORY_REGISTER.register("waterbending", Waterbending::new);
    private static final DeferredHolder<SkillCategory, Element> EARTHBENDING = Registries.SKILL_CATEGORY_REGISTER.register("earthbending", Earthbending::new);
    private static final DeferredHolder<SkillCategory, Element> FIREBENDING = Registries.SKILL_CATEGORY_REGISTER.register("firebending", Firebending::new);

    public static Element AIR, WATER, EARTH, FIRE;

    public static Element get(ResourceLocation id) {
        return (Element) Registries.SKILL_CATEGORIES.get(id);
    }

    public static Element random() {
        int pick = new Random().nextInt(4);
        return switch (pick) {
            case 0 -> AIR;
            case 1 -> WATER;
            case 2 -> EARTH;
            default -> FIRE;
        };
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
