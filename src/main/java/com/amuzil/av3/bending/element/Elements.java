package com.amuzil.av3.bending.element;

import com.amuzil.av3.Avatar;
import com.amuzil.magus.skill.SkillCategory;
import com.amuzil.av3.bending.element.air.Airbending;
import com.amuzil.av3.bending.element.earth.Earthbending;
import com.amuzil.av3.bending.element.fire.Firebending;
import com.amuzil.av3.bending.element.water.Waterbending;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.function.Supplier;


@Mod.EventBusSubscriber(modid = Avatar.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Elements {
    public static final HashMap<String, Element> ALL_FOUR = new HashMap<>();

    public static DeferredRegister<SkillCategory> SKILL_CATEGORY_REGISTER = DeferredRegister.create(ResourceLocation.parse("skill_categories"), Avatar.MOD_ID);
    public static Supplier<IForgeRegistry<SkillCategory>> SKILL_CATEGORIES = SKILL_CATEGORY_REGISTER.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<Element> AIRBENDING = SKILL_CATEGORY_REGISTER.register("airbending", Airbending::new);
    public static final RegistryObject<Element> WATERBENDING = SKILL_CATEGORY_REGISTER.register("waterbending", Waterbending::new);
    public static final RegistryObject<Element> EARTHBENDING = SKILL_CATEGORY_REGISTER.register("earthbending", Earthbending::new);
    public static final RegistryObject<Element> FIREBENDING = SKILL_CATEGORY_REGISTER.register("firebending", Firebending::new);

    public static Element AIR, WATER, EARTH, FIRE;

    public static Element get(ResourceLocation id) {
        return (Element) SKILL_CATEGORIES.get().getValue(id);
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
