package com.amuzil.omegasource.entity.modules;

import com.amuzil.omegasource.entity.AvatarEntity;
import net.minecraft.nbt.CompoundTag;

import javax.swing.text.html.parser.Entity;

public interface IEntityModule {

    String id();

    void init(AvatarEntity entity);

    void tick(AvatarEntity entity);

    void save(CompoundTag nbt);

    void load(CompoundTag nbt);

}
