package com.amuzil.magus.skill.traits.skilltraits;

import com.amuzil.magus.skill.traits.SkillTrait;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;


public class PushTrait extends SkillTrait {

    private PushType type;

    public PushTrait(String name, PushType type) {
        super(name);
        this.type = type;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        tag.putString("value", type.name());
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        super.deserializeNBT(provider, nbt);
        type = PushType.valueOf(nbt.getString("value"));
    }

    public void setType(PushType type) {
        this.type = type;
        markDirty();
    }

    public PushType getType() {
        return type;
    }

    @Override
    public void reset() {
        super.reset();
        setType(PushType.NONE);
    }

    /** Shows the different levels of redstone pushing in increasing order of redstone.
     *
     */
    public enum PushType {
        NONE,
        REDSTONE,
        STONE,
        IRON_DOOR,
        IRON_TRAPDOOR;
    }
}
