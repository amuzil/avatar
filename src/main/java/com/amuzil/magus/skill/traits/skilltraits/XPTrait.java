package com.amuzil.magus.skill.traits.skilltraits;

import com.amuzil.magus.skill.traits.SkillTrait;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;


public class XPTrait extends SkillTrait {

    private double xp;

    public XPTrait(String name, double xp) {
        super(name);
        this.xp = xp;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        tag.putDouble("value", xp);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        super.deserializeNBT(provider, nbt);
        xp = nbt.getDouble("value");
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
