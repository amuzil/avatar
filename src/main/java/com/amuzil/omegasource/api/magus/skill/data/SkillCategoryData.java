package com.amuzil.omegasource.api.magus.skill.data;

import com.amuzil.omegasource.api.magus.registry.Registries;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.skill.traits.DataTrait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;


public class SkillCategoryData implements DataTrait {

    private ResourceLocation id;
    private boolean canUse = false;
    private boolean isDirty = false;

    public SkillCategoryData(ResourceLocation id) {
        this.id = id;
    }

    public SkillCategoryData(SkillCategory category) {
        this(category.getId());
    }

    @Override
    public String name() {
        return getId() + "_skillCategory";
    }

    public ResourceLocation getId() {
        return id;
    }

    public SkillCategory getSkillCategory() {
        return Registries.SKILL_CATEGORIES.get().getValue(getId());
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

    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
        markDirty();
    }

    public boolean canUse() {
        return this.canUse;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Skill Category", id.toString());
        tag.putBoolean("Can Use", canUse);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.id = ResourceLocation.tryParse(tag.getString("Skill Category"));
        this.canUse = tag.getBoolean("Can Use");
    }
}
