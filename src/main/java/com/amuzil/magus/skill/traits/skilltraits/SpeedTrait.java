package com.amuzil.magus.skill.traits.skilltraits;

import com.amuzil.magus.skill.traits.SkillTrait;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;


public class SpeedTrait extends SkillTrait {

    private double speed = 1;

    public SpeedTrait(String name, double speed) {
        super(name);
        this.speed = speed;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        tag.putDouble("value", speed);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        super.deserializeNBT(provider, nbt);
        speed = nbt.getDouble("value");
    }

    public void setSpeed(double speed) {
        this.speed = speed;
        markDirty();
    }

    public double getSpeed() {
        return speed;
    }

    @Override
    public void reset() {
        super.reset();
        setSpeed(0);
    }
}
