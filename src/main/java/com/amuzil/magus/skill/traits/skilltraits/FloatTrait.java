package com.amuzil.magus.skill.traits.skilltraits;

import com.amuzil.magus.skill.traits.SkillTrait;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class FloatTrait extends SkillTrait {

    private float val;

    public FloatTrait(String name, float val) {
        super(name);
        this.val = val;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        tag.putFloat("value", val);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        super.deserializeNBT(provider, nbt);
        val = nbt.getFloat("value");
    }

    public void setValue(float val) {
        this.val = val;
        markDirty();
    }

    public double getValue() {
        return val;
    }

    @Override
    public void reset() {
        super.reset();
        setValue(0);
    }
}
