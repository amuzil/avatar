package com.amuzil.magus.registry;

import com.amuzil.av3.Avatar;
import com.amuzil.magus.form.Form;
import com.amuzil.magus.skill.Skill;
import com.amuzil.magus.skill.SkillCategory;
import com.amuzil.magus.skill.traits.DataTrait;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


/**
 * All custom registries go here.
 */
@EventBusSubscriber(modid = Avatar.MOD_ID)
public class Registries {
    // === Registries ===
//    public static final DeferredRegister<Form> FORMS =
//            DeferredRegister.create(Avatar.id("forms"), Avatar.MOD_ID);
    public static Registry<Form> FORMS;
    public static final DeferredRegister<Skill> SKILL_REGISTER =
            DeferredRegister.create(Avatar.id("skills"), Avatar.MOD_ID);
    public static final DeferredRegister<SkillCategory> SKILL_CATEGORY_REGISTER =
            DeferredRegister.create(Avatar.id("skill_categories"), Avatar.MOD_ID);
    public static final Registry<Skill> SKILLS =
            SKILL_REGISTER.makeRegistry((builder) -> builder.sync(true));
    public static final Registry<SkillCategory> SKILL_CATEGORIES =
            SKILL_CATEGORY_REGISTER.makeRegistry((builder) -> builder.sync(true));

    // === Local Containers ===
    private static final HashMap<String, Form> forms = new HashMap<>();
    private static final List<SkillCategory> categories = new ArrayList<>();
    private static final HashMap<String, Supplier<Skill>> skills = new HashMap<>();
    private static final Map<String, Supplier<? extends Skill>> skillSuppliers = new HashMap<>();
    private static final List<DataTrait> traits = new ArrayList<>();

    // === Accessors ===
    public static List<DataTrait> getTraits() {
        return traits;
    }

    public static HashMap<String, Form> getForms() {
        return forms;
    }

    public static Form getForm(String name) {
        return forms.get(name);
    }

    public static List<SkillCategory> getSkillCategories() {
        return categories;
    }

    public static List<Skill> getSkills() {
        return skills.values().stream().map(Supplier::get).toList();
    }

    public static Skill getSkill(ResourceLocation id) {
        Supplier<? extends Skill> sup = skillSuppliers.get(id.toString());
        return sup != null ? sup.get() : null;
    }

    // === Registration Helpers ===
    public static void registerSkillCategory(SkillCategory skillCategory) {
        categories.add(skillCategory);
    }

    public static Supplier<? extends Skill> registerSkill(Supplier<? extends Skill> skillSup) {
        String name = skillSup.get().name();
        Supplier<Skill> skillRegistryObject = SKILL_REGISTER.register(name, skillSup);
        String namespace = Avatar.id(name).toString();
        skills.put(namespace, skillRegistryObject);
        skillSuppliers.put(namespace, skillSup);
        return skillRegistryObject;
    }

    public static void registerForm(Form form) {
        forms.put(form.name(), form);
    }

    @SubscribeEvent
    public static void onNewRegistryEvent(NewRegistryEvent event) {
        // Forms
        RegistryBuilder<Form> formRegistryBuilder = new RegistryBuilder<>(ResourceKey.createRegistryKey(Avatar.id("forms")));
        formRegistryBuilder.defaultKey(Avatar.id("forms"));
        FORMS = event.create(formRegistryBuilder);
    }

    @SubscribeEvent
    public static void gameRegistry(RegisterEvent event) {
        // Forms
        if (event.getRegistryKey().equals(FORMS.key())) {
            event.register(FORMS.key(), registry -> {
                for (Form form: forms.values())
                    registry.register(Avatar.id(form.name()), form);
            });
        }
    }
}
