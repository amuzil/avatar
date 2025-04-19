package com.amuzil.omegasource.api.magus.skill.traits.skilltraits;

import com.amuzil.omegasource.api.magus.skill.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;

public class RangeTrait extends SkillTrait {

    private double range;

    public RangeTrait(double range, String name) {
        super(name);
        this.range = range;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putDouble(getName(), range);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        range = nbt.getDouble(getName());
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
