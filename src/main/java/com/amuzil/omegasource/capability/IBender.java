package com.amuzil.omegasource.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;


public interface IBender extends INBTSerializable<CompoundTag> {

    String getElement();

    void setElement(String element);

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag tag);
}
