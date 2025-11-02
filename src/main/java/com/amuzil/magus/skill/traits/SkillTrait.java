package com.amuzil.magus.skill.traits;

import net.minecraft.nbt.CompoundTag;


public class SkillTrait implements DataTrait, Cloneable {
    private String name;
    private boolean isDirty = false;

    public SkillTrait(String name) {
        this.name = name;
        markDirty();
    }

    @Override
    public SkillTrait clone() {
        try {
            return (SkillTrait) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Failed to clone SkillTrait, cloning not supported!", e);
        }
    }

    @Override
    public String toString() {
        return String.format("SkillTrait[ %s ]", name);
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
