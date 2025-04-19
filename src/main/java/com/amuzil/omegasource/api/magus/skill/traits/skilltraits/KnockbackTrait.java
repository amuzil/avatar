package com.amuzil.omegasource.api.magus.skill.traits.skilltraits;

import com.amuzil.omegasource.api.magus.skill.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;

public class KnockbackTrait extends SkillTrait {

    private double knockback;

    public KnockbackTrait(double knockback, String name) {
        super(name);
        this.knockback = knockback;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putDouble(getName(), knockback);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        knockback = nbt.getDouble(getName());
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
