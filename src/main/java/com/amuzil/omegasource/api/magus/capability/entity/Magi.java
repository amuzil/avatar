package com.amuzil.omegasource.api.magus.capability.entity;

import com.amuzil.omegasource.api.magus.capability.CapabilityHandler;
import com.amuzil.omegasource.api.magus.condition.conditions.FormCondition;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.radix.RadixTree;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.api.magus.skill.data.SkillCategoryData;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.traits.SkillTrait;
import com.amuzil.omegasource.api.magus.registry.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Magi {
    private final LivingEntity magi;

    //    @OnlyIn(Dist.DEDICATED_SERVER) // Prevents runClient from RUNNING
    private RadixTree activationTree;

    // These are magi specific traits.
    private List<SkillData> skillData;
    private List<SkillCategoryData> skillCategoryData;
    public FormPath formPath;
    private Skill currentlySelected;
    private FormCondition formConditionHandler;
    private boolean isDirty;

    // Change this to use an int - 0 for should start, 1 for should run, 2 for should stop,
    // -1 for default/idle. If I need multiple states, then use bits; 000 for idle, and then
    // 1xx is should start, x1x is should run, xx1 is should stop

    public Magi(LivingEntity entity) {
        this.magi = entity;

        // Initialise skilldata.
        this.skillData = new ArrayList<>();
        for (Skill skill : Registries.getSkills()) {
            skillData.add(new SkillData(skill));
        }

        // Testing...
        skillData.forEach(skillData1 -> skillData1.setCanUse(true));
        this.formPath = new FormPath();
        this.currentlySelected = null;
        this.formConditionHandler = new FormCondition();
        markDirty();
    }

    public void registerFormCondition() {
        formConditionHandler.register("FormCondition", () -> {
            ActiveForm activeForm = new ActiveForm(formConditionHandler.form(), formConditionHandler.active());
            formPath.update(activeForm);
            markDirty();
        }, () -> {
            if (!formPath.isActive()) {
                formPath.clear();
                markDirty();
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
            if (entity instanceof Player) {
//                Avatar.LOGGER.warn("Living Data cap is null.");
//                Thread.dumpStack();
            }
            return null;
        }
        return cap.getMagi(entity);
    }

    public static boolean isEntitySupported(Entity entity) {
        return entity instanceof LivingEntity;
    }

    public Skill currentlySelected() {
        return this.currentlySelected;
    }

    // Need to sync this with given skills, traits, e.t.c
    public boolean isDirty() {
        return this.isDirty;
    }

    public void markDirty() {
        this.isDirty = true;
    }

    public void setClean() {
        this.isDirty = false;
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

    public Skill.SkillState getSkillState(Skill skill) {
        return getSkillData(skill.getId()).getState();
    }

    public SkillData getSkillData(String id) {
        ResourceLocation loc = ResourceLocation.parse(id);
        return getSkillData(loc);
    }

    // Called per tick
    public void onUpdate() {
        if (getMagi() instanceof Player) {
            List<Skill> skills = Registries.getSkills();
//            getSkillData(Firebending.FIRE_STRIKE_SKILL).getTrait("speed", SpeedTrait.class).setSpeed(4.5f);
            for (Skill skill : skills) {
//                RadixTree.getLogger().debug("Skill: " + skill);
                if (getSkillData(skill).canUse()) {
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
            if (skillCategoryData != null)
                skillCategoryData.forEach(catData -> tag.put(catData.name(), catData.serializeNBT()));
            if (skillData != null)
                skillData.forEach(sData -> tag.put(sData.name(), sData.serializeNBT()));
            if (formPath != null)
                tag.put("Form Path", formPath.serializeNBT());
        }
        return tag;
    }

    public void deserialiseNBT(CompoundTag tag) {
//        complexForms.forEach(activeForm -> activeForm.deserializeNBT(tag.getCompound(activeForm.form().name())));
        if (skillCategoryData != null)
            skillCategoryData.forEach(catData -> catData.deserializeNBT(tag.getCompound(catData.name())));
        if (skillData != null)
            skillData.forEach(sData -> sData.deserializeNBT(tag.getCompound(sData.name())));
        if (formPath != null)
            formPath.deserializeNBT(tag);
    }
}
