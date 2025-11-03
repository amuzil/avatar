package com.amuzil.magus.skill.traits.skilltraits;

import com.amuzil.magus.skill.traits.SkillTrait;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;


public class RangeTrait extends SkillTrait {

    private double range;

    public RangeTrait(String name, double range) {
        super(name);
        this.range = range;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        tag.putDouble("value", range);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        super.deserializeNBT(provider, nbt);
        range = nbt.getDouble("value");
    }

    public void setRange(double range) {
        this.range = range;
        markDirty();
    }

    public double getRange() {
        return range;
    }

    @Override
    public void reset() {
        super.reset();
        setRange(0);
    }
}
