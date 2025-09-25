package com.amuzil.omegasource.entity.api;

import com.amuzil.omegasource.entity.AvatarEntity;
import net.minecraft.nbt.CompoundTag;

public interface IEntityModule {

    String id();

    void init(AvatarEntity entity);

    void tick(AvatarEntity entity);

    void save(CompoundTag nbt);

    void load(CompoundTag nbt);

}
