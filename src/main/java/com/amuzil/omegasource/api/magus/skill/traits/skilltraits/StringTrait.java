package com.amuzil.omegasource.api.magus.skill.traits.skilltraits;

import com.amuzil.omegasource.api.magus.skill.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;

public class StringTrait extends SkillTrait {

    private String info;

    public StringTrait(String name, String info) {
        super(name);
        this.info = info;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putString("value", info);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        info = nbt.getString("value");
    }

    public void setInfo(String info) {
        this.info = info;
        markDirty();
    }

    public String getInfo() {
        return info;
    }

    //Probably won't be used, but just in case.
    @Override
    public void reset() {
        super.reset();
        setInfo("");
    }
}
