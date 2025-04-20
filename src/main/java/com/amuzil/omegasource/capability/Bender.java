package com.amuzil.omegasource.capability;

import com.amuzil.omegasource.api.magus.condition.conditions.FormCondition;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.radix.RadixTree;
import com.amuzil.omegasource.api.magus.registry.Registries;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.skill.data.SkillCategoryData;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.traits.DataTrait;
import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.bending.element.Elements;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class Bender implements IBender {
    private String element = "fire";
    private final LivingEntity entity;
    private boolean isDirty = true; // Flag to indicate if data was changed
    public FormPath formPath;
    private FormCondition formConditionHandler;

    // Persistent data
    private Element activeElement = Elements.FIRE; // Currently active element
    private final List<DataTrait> dataTraits = new ArrayList<>();
    private final List<SkillData> skillData = new ArrayList<>();
    private final List<SkillCategoryData> skillCategoryData = new ArrayList<>();;

    public Bender(LivingEntity entity) {
        this.entity = entity;

        for (Skill skill : Registries.skills)
            skillData.add(new SkillData(skill));

        for (SkillCategory category : Registries.categories)
            skillCategoryData.add(new SkillCategoryData(category));

        skillData.forEach(skillData1 -> skillData1.setCanUse(true));
        skillCategoryData.forEach(skillCategoryData1 -> skillCategoryData1.setCanUse(true));
        this.formConditionHandler = new FormCondition();
        markDirty();
    }

    public void registerFormCondition() {
        formConditionHandler.register("FormCondition", () -> {
            ActiveForm activeForm = new ActiveForm(formConditionHandler.form(), formConditionHandler.active());
            formPath.update(activeForm);
            markDirty();
            if (entity.level().isClientSide()) {
                RadixTree.getLogger().debug("Simple Forms: {}", formPath.simple());
                RadixTree.getLogger().debug("Complex Forms: {}", formPath.complex());
            }
        }, () -> {
            if (!formPath.isActive()) {
                formPath.clear();
                markDirty();
                if (entity.level().isClientSide())
                    RadixTree.getLogger().debug("Complex Forms Timed Out");
            }
        });
    }

    public void unregisterFormCondition() {
        formConditionHandler.unregister();
    }

    @Override
    public String getElement() {
        return element;
    }

    @Override
    public void setElement(String element) {
        this.element = element;
        markDirty();
    }

    @Override
    public void markDirty() {
        this.isDirty = true;
    }

    @Override
    public void markClean() {
        this.isDirty = false;
    }

    @Override
    public boolean isDirty() {
        return this.isDirty;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Element", element);
        tag.putString("Active Element", activeElement.name());
        skillCategoryData.forEach(catData -> tag.put(catData.name(), catData.serializeNBT()));
        skillData.forEach(sData -> tag.put(sData.name(), sData.serializeNBT()));
        System.out.println("[Bender] Serializing NBT: " + tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.element = tag.getString("Element");
        this.activeElement = Elements.ALL_FOUR.get(tag.getString("Active Element"));
        skillCategoryData.forEach(catData -> catData.deserializeNBT(tag.getCompound(catData.name())));
        skillData.forEach(sData -> sData.deserializeNBT(tag.getCompound(sData.name())));
        System.out.println("[Bender] Deserializing NBT: " + tag);
    }

    @Override
    public String toString() {
        return "Bender[ " + activeElement.name() + " ]";
    }

    public void onUpdate() {
        if (entity instanceof Player) {
            List<Skill> skills = Registries.getSkills();
            for (Skill skill : skills) {
                if (getSkillData(skill).canUse()) {
                    skill.tick(entity, formPath);
                }
            }
        }
    }

    public SkillData getSkillData(Skill skill) {
        return getSkillData(skill.getId());
    }

    public SkillData getSkillData(ResourceLocation id) {
        return skillData.stream().filter(skillData1 -> skillData1.getSkillId().equals(id)).toList().get(0);
    }

    public static @Nullable IBender getBender(LivingEntity entity) {
        return entity.getCapability(AvatarCapabilities.BENDER)
                .orElse(null);
    }
}
