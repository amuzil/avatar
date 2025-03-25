package com.amuzil.omegasource.api.magus.skill.utils.traits.skilltraits;

import com.amuzil.omegasource.api.magus.skill.utils.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;

public class PotionLevelTrait extends SkillTrait {

    private int level;

    public PotionLevelTrait(int level, String name) {
        super(name);
        this.level = level;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putInt(getName(), level);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        level = nbt.getInt(getName());
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
