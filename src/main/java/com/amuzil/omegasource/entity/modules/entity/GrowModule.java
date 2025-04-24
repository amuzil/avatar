package com.amuzil.omegasource.entity.modules.entity;

import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.AvatarProjectile;
import com.amuzil.omegasource.entity.modules.IEntityModule;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class GrowModule implements IEntityModule {

    String id = "grow";

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {

    }

    @Override
    public void tick(AvatarEntity entity) {
        if (!(entity.owner() instanceof LivingEntity) || !(entity instanceof AvatarProjectile))
            return;
        AvatarProjectile proj = (AvatarProjectile) entity;
        float maxSize = (float) entity.getTrait("max_size", SizeTrait.class).getSize();
        int life = entity.maxLifetime() - entity.tickCount;
        if (proj.width() < maxSize) {
            proj.setHeight(proj.height() + (maxSize - proj.height()) / life);
            proj.setWidth(proj.width() + (maxSize - proj.width()) / life);
        }
    }

    @Override
    public void save(CompoundTag nbt) {
        nbt.putString("ID", id());
    }

    @Override
    public void load(CompoundTag nbt) {
        id = nbt.getString("ID");
    }
}
