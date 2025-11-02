package com.amuzil.av3.api.magus.skill.traits.skilltraits;

import com.amuzil.av3.api.magus.skill.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;


/**
 * The most generic trait of all time. Works for:
 * Fire time, charging, lifetime, potion duration, e.t.c.
 */
public class TimedTrait extends SkillTrait {
    private int time;

    public TimedTrait(String name, int time) {
        super(name);
        this.time = time;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putInt("value", time);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        time = nbt.getInt("value");
    }

    public void setTime(int time) {
        this.time = time;
        markDirty();
    }

    public int getTime() {
        return time;
    }

    /**
     * -1 instead of 0 here, because 0 implies a duration of nothing, whereas
     * -1 means never iterate at all.
     */
    @Override
    public void reset() {
        super.reset();
        setTime(-1);
    }
}
