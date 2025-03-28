package com.amuzil.omegasource.api.magus.skill.utils.capability.entity;

import com.amuzil.omegasource.api.magus.condition.conditions.FormCondition;
import com.amuzil.omegasource.api.magus.radix.RadixTree;
import com.amuzil.omegasource.api.magus.skill.FormPath;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.api.magus.skill.utils.capability.CapabilityHandler;
import com.amuzil.omegasource.api.magus.skill.utils.data.SkillCategoryData;
import com.amuzil.omegasource.api.magus.skill.utils.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.utils.traits.DataTrait;
import com.amuzil.omegasource.api.magus.skill.utils.traits.SkillTrait;
import com.amuzil.omegasource.bending.form.ActiveForm;
import com.amuzil.omegasource.registry.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class Magi {
    private final Data capabilityData;
    private final LivingEntity magi;

//    @OnlyIn(Dist.DEDICATED_SERVER) // Prevents runClient from RUNNING
    private RadixTree activationTree;

    // These are magi specific traits.
    private List<SkillData> skillData;
    private List<SkillCategoryData> skillCategoryData;
    public LinkedList<ActiveForm> activeForms;
    public FormPath formPath;
    private Skill currentlySelected;
    private FormCondition formConditionHandler;

    // Change this to use an int - 0 for should start, 1 for should run, 2 for should stop,
    // -1 for default/idle. If I need multiple states, then use bits; 000 for idle, and then
    // 1xx is should start, x1x is should run, xx1 is should stop
    private HashMap<String, Integer> skillStatuses = new HashMap<>();

    public Magi(Data capabilityData, LivingEntity entity) {
        this.capabilityData = capabilityData;
        this.magi = entity;
        this.activeForms = new LinkedList<>();

        // Initialise skilldata.
        this.skillData = new ArrayList<>();
        for (Skill skill : Registries.skills)
            skillData.add(new SkillData(skill));

        // Testing...
        skillData.forEach(skillData1 -> skillData1.setCanUse(true));
        this.formPath = new FormPath();
        this.currentlySelected = null;
        this.formConditionHandler = new FormCondition();
    }

    public void registerFormCondition() {
        formConditionHandler.register("formCondition", () -> {
            ActiveForm activeForm = new ActiveForm(formConditionHandler.form(), formConditionHandler.active());
            formPath.update(activeForm);
            if (magi.level().isClientSide()) {
                RadixTree.getLogger().debug("Simple Forms: {}", formPath.simple());
                RadixTree.getLogger().debug("Complex Forms: {}", formPath.complex());
            }
        },  () -> {
            if (!formPath.isActive())
                formPath.clear();
            if (magi.level().isClientSide()) {
                RadixTree.getLogger().debug("Simple Forms: {}", formPath.simple());
                RadixTree.getLogger().debug("Complex Forms Timed Out: {}", formPath.complex());
            }
        });
    }

    public void unregisterFormCondition() {
        formConditionHandler.unregister();
    }

    @Nullable
    public static Magi get(LivingEntity entity) {
        LivingDataCapability.LivingDataCapabilityImp cap = ((LivingDataCapability.LivingDataCapabilityImp) (CapabilityHandler.getCapability(entity, CapabilityHandler.LIVING_DATA)));
        if (cap == null) {
            // Capability isn't ready yet.
            return null;
        }
        return cap.getMagi(entity);
    }

    public static boolean isEntitySupported(Entity entity) {
        return entity instanceof LivingEntity;
    }

    public LivingDataCapability.LivingDataCapabilityImp getMagusData() {
        return (LivingDataCapability.LivingDataCapabilityImp) capabilityData;
    }

    public Skill currentlySelected() {
        return this.currentlySelected;
    }

    // Need to sync this with given skills, traits, e.t.c
    public boolean isDirty() {
        return false;
    }

    public List<DataTrait> getTraits() {
        return getMagusData().getTraits();
    }

    public List<SkillTrait> getSkillTraits(Skill skill) {
        // Get skill data based on the skill,
        // then get the list of its traits.
        // Empty for now...
        return getSkillData(skill).getSkillTraits();
    }

    // Returns appropriate SkillData from a list of SkillData
    public SkillData getSkillData(Skill skill) {
        return getSkillData(skill.getId());
    }

    public SkillData getSkillData(ResourceLocation id) {
        return skillData.stream().filter(skillData1 -> skillData1.getSkillId().equals(id)).toList().get(0);
    }

    public SkillData getSkillData(String id) {
        ResourceLocation loc = new ResourceLocation(id);
        return getSkillData(loc);
    }

    // Called per tick
    public void onUpdate() {
//        if (!activeForms.isEmpty())
//            RadixTree.getLogger().info("Magi OnUpdate: " + activeForms);

        if (getMagi() instanceof Player) {
            List<Skill> skills = Registries.getSkills();
//            RadixTree.getLogger().debug("Skill Registry Size: " + skills.size());
            for (Skill skill : skills) {
//                RadixTree.getLogger().debug("Skill: " + skill);
                if (getSkillData(skill).canUse()) {
                    // TODO: Make sure this works; blame Aidan if something needs to be client-side
//                    if (!getMagi().level().isClientSide)
                        skill.tick(getMagi(), formPath);
                }
            }
        }
    }

    public void onDeath() {}

    public LivingEntity getMagi() {
        return this.magi;
    }

    // We don't serialise `traits`, because those are serialised in the cap itself.
    // Wrapper allows us to access.
    public CompoundTag serialiseNBT() {
        CompoundTag tag = new CompoundTag();
        if (isDirty()) {
            // TODO: Figure out if I need to use the returned tags from each of these values....
//            complexForms.forEach(activeForm -> tag.put(activeForm.form().name(), activeForm.serializeNBT()));
            skillCategoryData.forEach(catData -> tag.put(catData.getName(), catData.serializeNBT()));
            skillData.forEach(sData -> tag.put(sData.getName(), sData.serializeNBT()));
            formPath.serializeNBT();
            System.out.println("Serialise: " + formPath.complex());
        }
        return tag;
    }

    public void deserialiseNBT(CompoundTag tag) {
//        complexForms.forEach(activeForm -> activeForm.deserializeNBT(tag.getCompound(activeForm.form().name())));
        skillCategoryData.forEach(catData -> catData.deserializeNBT(tag.getCompound(catData.getName())));
        skillData.forEach(sData -> sData.deserializeNBT(tag.getCompound(sData.getName())));
        formPath.deserializeNBT(tag);
        System.out.println("Deserialise: " + formPath.complex());
    }
}
