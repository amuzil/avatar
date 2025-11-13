package com.amuzil.magus.registry;

import com.amuzil.av3.Avatar;
import com.amuzil.magus.form.Form;
import com.amuzil.magus.skill.Skill;
import com.amuzil.magus.skill.SkillCategory;
import com.amuzil.magus.skill.traits.DataTrait;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import static com.amuzil.av3.bending.form.BendingForms.*;

/**
 * All custom registries go here.
 */
public class Registries {
    // === Registries ===
    public static final DeferredRegister<Form> FORMS_REGISTER = DeferredRegister.create(Avatar.id("forms"), Avatar.MOD_ID);
    public static final DeferredRegister<Skill> SKILL_REGISTER = DeferredRegister.create(Avatar.id("skills"), Avatar.MOD_ID);
    public static final DeferredRegister<SkillCategory> SKILL_CATEGORY_REGISTER = DeferredRegister.create(Avatar.id("skill_categories"), Avatar.MOD_ID);
    public static final Registry<Form> FORMS = FORMS_REGISTER.makeRegistry((builder) -> builder.sync(true));
    public static final Registry<Skill> SKILLS = SKILL_REGISTER.makeRegistry((builder) -> builder.sync(true));
    public static final Registry<SkillCategory> SKILL_CATEGORIES = SKILL_CATEGORY_REGISTER.makeRegistry((builder) -> builder.sync(true));

    // === Local Containers ===
    private static final List<SkillCategory> categories = new ArrayList<>();
    private static final HashMap<String, Supplier<Skill>> skills = new HashMap<>();
    private static final HashMap<String, Supplier<? extends Skill>> skillSuppliers = new HashMap<>();
    private static final List<DataTrait> traits = new ArrayList<>();

    // === Accessors ===
    public static List<DataTrait> getTraits() {
        return traits;
    }

    public static Form getForm(String name) {
        return FORMS.get(Avatar.id(name));
    }

    public static List<Form> getForms() {
        return FORMS.stream().toList();
    }

    public static Skill getSkill(ResourceLocation id) {
        Supplier<? extends Skill> sup = skillSuppliers.get(id.toString());
        return sup != null ? sup.get() : null;
    }

    public static List<Skill> getSkills() {
        return skills.values().stream().map(Supplier::get).toList();
    }

    public static List<SkillCategory> getSkillCategories() {
        return categories;
    }

    // === Registration Helpers ===
    public static void registerForm(Form form) {
        String name = form.name();
        FORMS_REGISTER.register(name, () -> form);
    }

    // TODO: Streamline this with reflection or something later
    public static void registerForms() {
        registerForm(NULL);
        registerForm(STRIKE);
        registerForm(BLOCK);
        registerForm(STEP);
        registerForm(PUSH);
        registerForm(PULL);
        registerForm(LEFT);
        registerForm(RIGHT);
        registerForm(RAISE);
        registerForm(LOWER);
        registerForm(ROTATE);
        registerForm(EXPAND);
        registerForm(COMPRESS);
        registerForm(SPLIT);
        registerForm(COMBINE);
        registerForm(ARC);
        registerForm(SHAPE);
        registerForm(FOCUS);
    }

    public static Supplier<? extends Skill> registerSkill(Supplier<? extends Skill> skillSup) {
        String name = skillSup.get().name();
        Supplier<Skill> skillRegistryObject = SKILL_REGISTER.register(name, skillSup);
        String namespace = Avatar.id(name).toString();
        skills.put(namespace, skillRegistryObject);
        skillSuppliers.put(namespace, skillSup);
        return skillRegistryObject;
    }

    public static void registerSkillCategory(SkillCategory skillCategory) {
        categories.add(skillCategory);
    }

    public static void printAll() {
        System.out.println("[Registries] " + Avatar.isClientOrServer(FMLEnvironment.dist.isClient()));
        System.out.print("| ");
        FORMS.forEach(form -> System.out.print(form.name() + " | "));
        System.out.print("\n| ");
        SKILL_CATEGORIES.forEach(skillCategory -> System.out.print(skillCategory.name() + " | "));
        System.out.print("\n| ");
        SKILLS.forEach(skill -> System.out.print(skill.name() + " | "));
        System.out.println("\n[Registries] Total Skills: " + SKILLS.size());
    }

//    @SubscribeEvent
//    public static void onNewRegistryEvent(NewRegistryEvent event) {
//        // Forms
//        RegistryBuilder<Form> formRegistryBuilder = new RegistryBuilder<>(ResourceKey.createRegistryKey(Avatar.id("forms")));
//        formRegistryBuilder.defaultKey(Avatar.id("forms"));
//        FORMS = event.create(formRegistryBuilder);
//    }

//    @SubscribeEvent
//    public static void gameRegistry(RegisterEvent event) {
//        // Forms
//        if (event.getRegistryKey().equals(FORMS.key())) {
//            event.register(FORMS.key(), registry -> {
//                for (Form form: forms.values())
//                    registry.register(Avatar.id(form.name()), form);
//            });
//        }
//    }
}
