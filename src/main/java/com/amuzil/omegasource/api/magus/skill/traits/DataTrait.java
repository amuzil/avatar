package com.amuzil.omegasource.api.magus.skill.traits;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;


public interface DataTrait extends INBTSerializable<CompoundTag> {

    String name();

    /* Remember to call these in *every* setter you have for each trait! */
    /* Methods for whether to save the data. */
    void markDirty();

    /* Do not use this ever. This is only used by LivingData upon unserialisation. */
    void markClean();

    boolean isDirty();

}
