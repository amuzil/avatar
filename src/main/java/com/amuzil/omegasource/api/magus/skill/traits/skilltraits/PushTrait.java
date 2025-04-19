package com.amuzil.omegasource.api.magus.skill.traits.skilltraits;

import com.amuzil.omegasource.api.magus.skill.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;

public class PushTrait extends SkillTrait {

    private PushType type;

    public PushTrait(PushType type, String name) {
        super(name);
        this.type = type;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putString(getName(), type.name());
        return super.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        type = PushType.valueOf(nbt.getString(getName()));
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
