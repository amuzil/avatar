package com.amuzil.magus.skill.traits.skilltraits;

import com.amuzil.magus.skill.traits.SkillTrait;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;


public class PotionLevelTrait extends SkillTrait {

    private int level;

    public PotionLevelTrait(String name, int level) {
        super(name);
        this.level = level;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        tag.putInt("value", level);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        super.deserializeNBT(provider, nbt);
        level = nbt.getInt("value");
    }

    public void setLevel(int level) {
        this.level = level;
        markDirty();
    }

    public int getLevel() {
        return level;
    }

    /**
     * 0 means a level 1 potion, so -1 means nothing at all.
     */
    @Override
    public void reset() {
        super.reset();
        setLevel(-1);
    }
}
