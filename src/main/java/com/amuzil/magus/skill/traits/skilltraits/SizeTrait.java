package com.amuzil.magus.skill.traits.skilltraits;

import com.amuzil.magus.skill.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;


public class SizeTrait extends SkillTrait {

    private double size;

    public SizeTrait(String name, float size) {
        super(name);
        this.size = size;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putDouble("size", size);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
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
