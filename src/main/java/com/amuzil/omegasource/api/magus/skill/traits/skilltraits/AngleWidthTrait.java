package com.amuzil.omegasource.api.magus.skill.traits.skilltraits;

import com.amuzil.omegasource.api.magus.skill.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;

/**
 * In degrees.
 */
public class AngleWidthTrait extends SkillTrait {

    //Between 0 and 360. Double for *very* specific angles.
    private double degrees;

    public AngleWidthTrait(double degrees, String name) {
        super(name);
        this.degrees = degrees;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putDouble(name(), degrees);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        degrees = nbt.getDouble(name());
    }

    public void setDegrees(double degrees) {
        this.degrees = degrees;
        markDirty();
    }

    public double getDegrees() {
        return degrees;
    }

    @Override
    public void reset() {
        super.reset();
        setDegrees(0);
    }
}
