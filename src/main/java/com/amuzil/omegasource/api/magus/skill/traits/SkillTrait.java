package com.amuzil.omegasource.api.magus.skill.traits;

import net.minecraft.nbt.CompoundTag;


public class SkillTrait implements DataTrait {
    private String name;
    private boolean isDirty = false;

    public SkillTrait(String name) {
        this.name = name;
        markDirty();
    }

    @Override
    public String name() {
        return name;
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
        tag.putString("name", name);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        markClean();
        name = nbt.getString("name");
    }

    /**
     * Resets stored values. Good for resetting things that are ticked during a Skill's use,
     * such as a combo count or timed duration.
     */
    public void reset() {
        markDirty();
    }
}
