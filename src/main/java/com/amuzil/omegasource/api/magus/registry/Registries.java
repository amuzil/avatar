package com.amuzil.omegasource.api.magus.registry;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.Form;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.skill.traits.DataTrait;
import com.amuzil.omegasource.bending.form.BendingForms;
import com.amuzil.omegasource.bending.element.Elements;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;


/**
 * All custom registries go here.
 */
@Mod.EventBusSubscriber(modid = Avatar.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registries {
    public static Supplier<IForgeRegistry<Form>> FORMS;
    public static DeferredRegister<Skill> SKILL_REGISTER = DeferredRegister.create(ResourceLocation.parse("skills"), Avatar.MOD_ID);
    public static Supplier<IForgeRegistry<Skill>> SKILLS = SKILL_REGISTER.makeRegistry(RegistryBuilder::new);
    public static Supplier<IForgeRegistry<SkillCategory>> SKILL_CATEGORIES;

    private static final List<Form> forms = new ArrayList<>();
    private static final HashMap<String, RegistryObject<Skill>> skills = new HashMap<>();
    private static final List<SkillCategory> categories = new ArrayList<>();
    private static final List<DataTrait> traits = new ArrayList<>();

    private static boolean initialized_bending = false;

    public static void init() {
        Elements.init();
    }

    public static List<Form> getForms() {
        return forms;
    }

    public static List<Skill> getSkills() {
        return skills.values().stream().map(c -> c.get()).toList();
    }

    public static List<SkillCategory> getSkillCategories() {
        return categories;
    }

    public static List<DataTrait> getTraits() {
        return traits;
    }

    public static void registerForm(Form form) {
        forms.add(form);
    }

    public static RegistryObject<? extends Skill> registerSkill(String name, Supplier<? extends Skill> skillSup) {
        RegistryObject<Skill> skillRegistryObject = SKILL_REGISTER.register(name, skillSup);
        String namespace = ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, name).toString();
        skills.put(namespace, skillRegistryObject);


        return skillRegistryObject;
    }

    public static void registerSkillCategory(SkillCategory skillCategory) {
        categories.add(skillCategory);
    }

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
//        RegistryBuilder<Skill> skills = new RegistryBuilder<>();
//        skills.setName(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "skills"));
//        SKILLS = event.create(skills);

        // Skill Categories
        RegistryBuilder<SkillCategory> categories = new RegistryBuilder<>();
        categories.setName(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "skill_categories"));
        SKILL_CATEGORIES = event.create(categories);
    }

    @SubscribeEvent
    public static void gameRegistry(RegisterEvent event) {
        // Ensure lists get populated to be added to Skills & Forms registry
        if (!initialized_bending) {
            BendingForms.init();
            initialized_bending = true;
        }

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
//        if (event.getRegistryKey().equals(SKILLS.get().getRegistryKey())) {
//            IForgeRegistry<Skill> registry = SKILLS.get();
//            ResourceKey<Registry<Skill>> resKey = registry.getRegistryKey();
//            event.register(resKey, helper -> {
//                for (Skill skill: skills)
//                    helper.register(skill.getId(), skill);
//            });
//        }

        // Skill Categories
        if (event.getRegistryKey().equals(SKILL_CATEGORIES.get().getRegistryKey())) {
            IForgeRegistry<SkillCategory> registry = SKILL_CATEGORIES.get();
            ResourceKey<Registry<SkillCategory>> resKey = registry.getRegistryKey();
            event.register(resKey, helper -> {
                for (SkillCategory category: categories)
                    helper.register(category.name(), category);
            });
        }
    }

    public static Skill getSkillByName(ResourceLocation id) {
        String namespace = id.toString();
        return skills.get(namespace).get();
    }

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
