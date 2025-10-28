package com.amuzil.omegasource.api.magus.registry;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.Form;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.skill.traits.DataTrait;
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
import java.util.Map;
import java.util.function.Supplier;


/**
 * All custom registries go here.
 */
@Mod.EventBusSubscriber(modid = Avatar.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registries {
    public static Supplier<IForgeRegistry<Form>> FORMS;

    public static DeferredRegister<Skill> SKILL_REGISTER = DeferredRegister.create(ResourceLocation.parse("skills"), Avatar.MOD_ID);
    public static Supplier<IForgeRegistry<Skill>> SKILLS = SKILL_REGISTER.makeRegistry(RegistryBuilder::new);

    // Do we still need these SkillCategory registries here?
    public static DeferredRegister<SkillCategory> SKILL_CATEGORY_REGISTER = DeferredRegister.create(ResourceLocation.parse("skill_categories"), Avatar.MOD_ID);
    public static Supplier<IForgeRegistry<SkillCategory>> SKILL_CATEGORIES = SKILL_CATEGORY_REGISTER.makeRegistry(RegistryBuilder::new);

    private static final HashMap<String, Form> forms = new HashMap<>();
    private static final Map<String, Supplier<? extends Skill>> skillSuppliers = new HashMap<>();
    private static final HashMap<String, RegistryObject<Skill>> skills = new HashMap<>();
    private static final List<SkillCategory> categories = new ArrayList<>();
    private static final List<DataTrait> traits = new ArrayList<>();

//    public static void init() {
//        Elements.init();
//    }

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
        return skills.values().stream().map(RegistryObject::get).toList();
    }

    public static Skill getSkill(ResourceLocation id) {
        Supplier<? extends Skill> sup = skillSuppliers.get(id.toString());
        return sup != null ? sup.get() : null;
    }

    public static Skill getRegisteredSkill(ResourceLocation id) {
        RegistryObject<Skill> obj = skills.get(id.toString());
        return obj != null ? obj.get() : null;
    }

    public static void registerForm(Form form) {
        forms.put(form.name(), form);
    }

    public static RegistryObject<? extends Skill> registerSkill(Supplier<? extends Skill> skillSup) {
        String name = skillSup.get().name();
        RegistryObject<Skill> skillRegistryObject = SKILL_REGISTER.register(name, skillSup);
        String namespace = ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, name).toString();
        skills.put(namespace, skillRegistryObject);
        skillSuppliers.put(namespace, skillSup);
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
    }

    @SubscribeEvent
    public static void gameRegistry(RegisterEvent event) {
        // Forms
        if (event.getRegistryKey().equals(FORMS.get().getRegistryKey())) {
            IForgeRegistry<Form> registry = FORMS.get();
            ResourceKey<Registry<Form>> resKey = registry.getRegistryKey();
            event.register(resKey, helper -> {
                for (Form form: forms.values())
                    helper.register(form.name(), form);
            });
        }
    }
}
