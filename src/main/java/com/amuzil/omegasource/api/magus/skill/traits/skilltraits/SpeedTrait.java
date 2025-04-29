package com.amuzil.omegasource.api.magus.skill.traits.skilltraits;

import com.amuzil.omegasource.api.magus.skill.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;


public class SpeedTrait extends SkillTrait {

    private double speed = 1;

    public SpeedTrait(double speed, String name) {
        super(name);
        this.speed = speed;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putDouble("value", speed);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
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
