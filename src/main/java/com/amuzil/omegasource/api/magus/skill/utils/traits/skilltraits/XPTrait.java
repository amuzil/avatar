package com.amuzil.omegasource.api.magus.skill.utils.traits.skilltraits;

import com.amuzil.omegasource.api.magus.skill.utils.traits.SkillTrait;
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
        tag.putDouble(getName(), xp);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        xp = nbt.getDouble(getName());
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
