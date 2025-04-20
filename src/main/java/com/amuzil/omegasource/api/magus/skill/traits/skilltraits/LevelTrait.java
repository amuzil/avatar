package com.amuzil.omegasource.api.magus.skill.traits.skilltraits;

import com.amuzil.omegasource.api.magus.skill.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;

public class LevelTrait extends SkillTrait {

    private int level;

    public LevelTrait(int level, String name) {
        super(name);
        this.level = level;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putInt(name(), level);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        level = nbt.getInt(name());
    }

    public void setLevel(int level) {
        this.level = level;
        markDirty();
    }

    public int getLevel() {
        return level;
    }

    //-1, because 0 is usually going to be unlocked/starting level in most mods.
    //-1 is locked.
    @Override
    public void reset() {
        super.reset();
        setLevel(-1);
    }
}
