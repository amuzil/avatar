package com.amuzil.magus.skill.data;

import com.amuzil.magus.registry.Registries;
import com.amuzil.magus.skill.SkillCategory;
import com.amuzil.magus.skill.traits.DataTrait;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;


public class SkillCategoryData implements DataTrait {

    private ResourceLocation id;
    private boolean canUse = true; // TODO - Temporary for testing
    private boolean isDirty = false;

    public SkillCategoryData(ResourceLocation id) {
        this.id = id;
    }

    public SkillCategoryData(SkillCategory category) {
        this(category.getId());
    }

    public ResourceLocation getId() {
        return id;
    }

    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
        markDirty();
    }

    public boolean canUse() {
        return this.canUse;
    }

    public SkillCategory getSkillCategory() {
        return Registries.SKILL_CATEGORIES.get().getValue(getId());
    }

    @Override
    public String name() {
        return getId() + "_skillCategory";
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
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Skill Category", id.toString());
        tag.putBoolean("Can Use", canUse);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        id = ResourceLocation.tryParse(tag.getString("Skill Category"));
        canUse = tag.getBoolean("Can Use");
    }
}
