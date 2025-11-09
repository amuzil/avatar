package com.amuzil.magus.skill.traits.skilltraits;

import com.amuzil.magus.skill.traits.SkillTrait;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;


public class KnockbackTrait extends SkillTrait {

    private double knockback;

    public KnockbackTrait(String name, double knockback) {
        super(name);
        this.knockback = knockback;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        tag.putDouble("value", knockback);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        super.deserializeNBT(provider, nbt);
        knockback = nbt.getDouble("value");
    }

    public void setKnockback(double knockback) {
        this.knockback = knockback;
        markDirty();
    }

    public double getKnockback() {
        return knockback;
    }

    @Override
    public void reset() {
        super.reset();
        setKnockback(0);
    }
}
