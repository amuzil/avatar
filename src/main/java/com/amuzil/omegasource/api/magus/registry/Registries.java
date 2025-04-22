package com.amuzil.omegasource.api.magus.registry;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.Form;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.api.magus.skill.traits.DataTrait;
import com.amuzil.omegasource.api.magus.skill.traits.SkillTrait;
import com.amuzil.omegasource.bending.BendingForms;
import com.amuzil.omegasource.bending.element.Elements;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


/**
 * All custom registries go here.
 */
@Mod.EventBusSubscriber(modid = Avatar.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registries {

    public static Supplier<IForgeRegistry<Form>> FORMS;
    public static Supplier<IForgeRegistry<Skill>> SKILLS;
    public static Supplier<IForgeRegistry<SkillCategory>> SKILL_CATEGORIES;
  //  public static Supplier<IForgeRegistry<DataTrait>> DATA_TRAITS;
    public static List<Form> forms = new ArrayList<>();
    public static List<Skill> skills = new ArrayList<>();
    public static List<SkillCategory> categories = new ArrayList<>();
    public static List<DataTrait> traits = new ArrayList<>();

    public static void init() {}

    public static List<Skill> getSkills() {
        return skills;
    }

    public static void registerForm(Form form) {
        forms.add(form);
    }

    public static void registerSkill(Skill skill) {
        skills.add(skill);
    }

    public static void registerSkillCategory(SkillCategory skillCategory) {
        categories.add(skillCategory);
    }

//    public static void registerTrait(DataTrait dataTrait) {
//        traits.add(dataTrait);
//    }

    /**
     * All skills will be registered first, along with skill categories.
     * Then, each skill will have its getTraits() method called, and each of its traits will be registered.
     */
    @SubscribeEvent
    public static void onRegistryRegister(NewRegistryEvent event) {
        // Forms
        RegistryBuilder<Form> formRegistryBuilder = new RegistryBuilder<>();
        formRegistryBuilder.setName(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "forms"));
        FORMS = event.create(formRegistryBuilder);

        // Skills
        RegistryBuilder<Skill> skills = new RegistryBuilder<>();
        skills.setName(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "skills"));
        SKILLS = event.create(skills);

        // Skill Categories
        RegistryBuilder<SkillCategory> categories = new RegistryBuilder<>();
        categories.setName(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "skill_categories"));
        SKILL_CATEGORIES = event.create(categories);

        // Data Traits
//        RegistryBuilder<DataTrait> traits = new RegistryBuilder<>();
//        traits.setName(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "data_traits"));
//        DATA_TRAITS = event.create(traits);
    }

    @SubscribeEvent
    public static void gameRegistry(RegisterEvent event) {
        // Ensure lists get populated to be added to Forms registry
        Elements.init();
        BendingForms.init();

        // Forms
        if (event.getRegistryKey().equals(FORMS.get().getRegistryKey())) {
            IForgeRegistry<Form> registry = FORMS.get();
            ResourceKey<Registry<Form>> resKey = registry.getRegistryKey();
            event.register(resKey, helper -> {
                for (Form form: forms)
                    helper.register(form.name(), form);
            });
        }

        // Skills
        if (event.getRegistryKey().equals(SKILLS.get().getRegistryKey())) {
            IForgeRegistry<Skill> registry = SKILLS.get();
            ResourceKey<Registry<Skill>> resKey = registry.getRegistryKey();
            event.register(resKey, helper -> {
                for (Skill skill: skills)
                    helper.register(skill.getId(), skill);
            });
        }

        // Skill Categories
        if (event.getRegistryKey().equals(SKILL_CATEGORIES.get().getRegistryKey())) {
            IForgeRegistry<SkillCategory> registry = SKILL_CATEGORIES.get();
            ResourceKey<Registry<SkillCategory>> resKey = registry.getRegistryKey();
            event.register(resKey, helper -> {
                for (SkillCategory category: categories)
                    helper.register(category.name(), category);
            });
        }

        // Data Traits
//        if (event.getRegistryKey().equals(DATA_TRAITS.get().getRegistryKey())) {
//            IForgeRegistry<DataTrait> registry = DATA_TRAITS.get();
//            ResourceKey<Registry<DataTrait>> resKey = registry.getRegistryKey();
//            // Registers every Data Trait for every skill included within Magus.
//            // Register other traits manually.
//            registerTraitsFromSkills(SKILLS.get().getValues().stream().toList(), event);
//            event.register(resKey, helper -> {
//                for (DataTrait trait: traits)
//                    helper.register(trait.name(), trait);
//            });
//        }
    }

    /**
     * Use this method to register the data traits of all registered skills.
     *
     * @param skills List of skills.
     * @param event  Registry event.
     * @param modID  ModID.
     */
//    public static void registerTraitsFromSkills(List<Skill> skills, RegisterEvent event, String modID) {
//        ResourceKey<Registry<DataTrait>> key = DATA_TRAITS.get().getRegistryKey();
//        IForgeRegistry<DataTrait> registry = DATA_TRAITS.get();
//        for (Skill skill : skills)
//            for (SkillTrait trait : skill.getTraits())
//                event.register(key, helper -> registry.register(ResourceLocation.parse(modID) + trait.name(), trait));
//
//    }

    /**
     * Same as the above method, but if you standardise your modID in your data,
     * then use this.
     *
     * @param skills Skills to register.
     * @param event  The registry event.
     */
//    public static void registerTraitsFromSkills(List<Skill> skills, RegisterEvent event) {
//        ResourceKey<Registry<DataTrait>> key = DATA_TRAITS.get().getRegistryKey();
//        IForgeRegistry<DataTrait> registry = DATA_TRAITS.get();
//        for (Skill skill : skills) {
//            System.out.println("REGISTER SKILL's TRAITS -> " + skill.getId());
//            for (SkillTrait trait : skill.getTraits()) {
//                event.register(key, helper -> registry.register(trait.name(), trait));
//            }
//        }
//    }

    @Mod.EventBusSubscriber(modid = Avatar.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeRegistries {
        @SubscribeEvent
        public static void onMissing(MissingMappingsEvent event) {
            //Data Traits

            //Skill Categories

            //Skills

            //Forms

            //Modifiers
        }
    }
}
