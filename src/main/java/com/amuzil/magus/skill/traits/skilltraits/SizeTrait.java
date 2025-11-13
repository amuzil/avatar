package com.amuzil.magus.skill.traits.skilltraits;

import com.amuzil.magus.skill.traits.SkillTrait;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;


public class SizeTrait extends SkillTrait {

    private double size;

    public SizeTrait(String name, float size) {
        super(name);
        this.size = size;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        tag.putDouble("size", size);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        super.deserializeNBT(provider, nbt);
        size = nbt.getDouble("size");
    }

    public void setSize(double size) {
        this.size = size;
        markDirty();
    }

    public double getSize() {
        return size;
    }

    @Override
    public void reset() {
        super.reset();
        setSize(0);
    }
}
