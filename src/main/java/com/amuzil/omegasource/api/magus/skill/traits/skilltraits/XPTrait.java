package com.amuzil.omegasource.api.magus.skill.traits.skilltraits;

import com.amuzil.omegasource.api.magus.skill.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;

public class XPTrait extends SkillTrait {

    private double xp;

    public XPTrait(double xp, String name) {
        super(name);
        this.xp = xp;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putDouble(name(), xp);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        xp = nbt.getDouble(name());
    }

    public void setXP(double xp) {
        this.xp = xp;
        markDirty();
    }

    public double getXp() {
        return xp;
    }

    @Override
    public void reset() {
        super.reset();
        setXP(0);
    }
}
