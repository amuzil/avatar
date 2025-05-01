package com.amuzil.omegasource.entity.modules.render;

import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.modules.IRenderModule;
import net.minecraft.nbt.CompoundTag;


public class ParticleModule implements IRenderModule {
    String id = "particle";

    @Override
    public String id() {
        return "";
    }

    @Override
    public void init(AvatarEntity entity) {}

    @Override
    public void tick(AvatarEntity entity) {

    }

    @Override
    public void save(CompoundTag nbt) {
        nbt.putString("ID", id);
    }

    @Override
    public void load(CompoundTag nbt) {
        this.id = nbt.getString("ID");
    }
}
