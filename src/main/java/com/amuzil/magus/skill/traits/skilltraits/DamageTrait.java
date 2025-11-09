package com.amuzil.magus.skill.traits.skilltraits;

import com.amuzil.magus.skill.traits.SkillTrait;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;


/**
 * Basic DamageTrait class for skills. Lets the user determine
 * what kind of damage it's for. E.g a Lightning Arc technique might take
 * multiple damage traits for the shockwave, chain hits, e.t.c.
 */
public class DamageTrait extends SkillTrait {

    private double damage;

    public DamageTrait(String name, double damage) {
        super(name);
        this.damage = damage;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        tag.putDouble("value", damage);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        super.deserializeNBT(provider, nbt);
        damage = nbt.getDouble("value");
    }

    public void setDamage(double damage) {
        this.damage = damage;
        markDirty();
    }

    public double getDamage() {
        return this.damage;
    }

    @Override
    public void reset() {
        super.reset();
        setDamage(0);
    }
}
