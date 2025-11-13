package com.amuzil.magus.skill.traits.skilltraits;

import com.amuzil.magus.skill.traits.SkillTrait;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;


/**
 * Simple string storage trait
 */
public class StringTrait extends SkillTrait {

    private String info;

    public StringTrait(String name, String info) {
        super(name);
        this.info = info;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        tag.putString("value", info);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        super.deserializeNBT(provider, nbt);
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
