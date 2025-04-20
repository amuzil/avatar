package com.amuzil.omegasource.api.magus.skill.data;

import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.skill.traits.DataTrait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;


public class SkillCategoryData implements DataTrait {

    ResourceLocation categoryId;
    private boolean canUse = false;
    private boolean isDirty = false;

    public SkillCategoryData(ResourceLocation categoryId) {
        this.categoryId = categoryId;
    }

    public SkillCategoryData(SkillCategory category) {
        this(category.getId());
    }

    @Override
    public String name() {
        return null;
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
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }
}
