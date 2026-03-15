package com.amuzil.magus.skill.traits.skilltraits;

import com.amuzil.av3.utils.maths.Easings;
import com.amuzil.magus.skill.traits.SkillTrait;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;


/**
 * Generic collision type handler class
 */
public class CollisionTrait extends SkillTrait {

    private final List<String> collisionTypes;

    public CollisionTrait(String name, List<String> collisionTypes) {
        super(name);
        this.collisionTypes = collisionTypes;
    }

    public CollisionTrait(String name, String... collisionTypes) {
        this(name, List.of(collisionTypes));
    }

    public CollisionTrait(String name) {
        this(name, new ArrayList<>());
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (String collisionType: collisionTypes)
            list.add(StringTag.valueOf(collisionType));
        tag.put("value", list);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.collisionTypes.clear();
        ListTag list = nbt.getList("value", Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++)
            collisionTypes.add(list.getString(i));
    }

    @Override
    public void reset() {
        super.reset();
        collisionTypes.clear();
    }

    /**
     * @return unmodifiable view of control points
     */
    public List<String> getTypes() {
        return collisionTypes;
    }

    public float evaluate(float t) {
        List<Float> xs = new ArrayList<>(), ys = new ArrayList<>();
        for (String p : collisionTypes) {

        }
        // Lmao what
        return Easings.bezier(xs, ys, t);
    }

}
