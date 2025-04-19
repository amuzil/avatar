package com.amuzil.omegasource.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;


public interface IBender extends INBTSerializable<CompoundTag> {

    String getElement();

    void setElement(String element);

    // Remember to call this in *every* data update!
    void markDirty();

    // Mark data as clean
    void markClean();

    // Check if data needs to be synced across client / server
    boolean isDirty();

    // Save data
    CompoundTag serializeNBT();

    // Load data
    void deserializeNBT(CompoundTag tag);
}
