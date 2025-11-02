package com.amuzil.magus.skill.traits.skilltraits;

import com.amuzil.magus.skill.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;


public class DirectionTrait extends SkillTrait {

    private Vec3 direction;

    public DirectionTrait(String name, Vec3 direction) {
        super(name);
        this.direction = direction;
    }

    public Vec3 direction() {
        return this.direction;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putDouble("X", direction.x);
        tag.putDouble("Y", direction.y);
        tag.putDouble("Z", direction.z);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        double x = nbt.getDouble("X");
        double y = nbt.getDouble("Y");
        double z = nbt.getDouble("Z");
        this.direction = new Vec3(x, y, z);
    }

    /**
     * Resets stored values. Good for resetting things that are ticked during a Skill's use,
     * such as a combo count or timed duration.
     */
    @Override
    public void reset() {
        super.reset();
        this.direction = Vec3.ZERO;
    }
}
