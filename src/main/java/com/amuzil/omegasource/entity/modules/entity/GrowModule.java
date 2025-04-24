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
        if (!(entity.owner() instanceof LivingEntity) || !(entity instanceof AvatarProjectile proj))
            return;
        int life = entity.maxLifetime();
        int age = entity.tickCount;
        if (life <= 0) return;

        // normalize [0,1]
        float t = (float) age / (float) life;
        if (t < 0.125f)
            t = 0f;
        t = net.minecraft.util.Mth.clamp(t, 0f, 1f);

        // quintic easing: 6t^5 - 15t^4 + 10t^3
        float ease = t * t * t * (10f + t * (-15f + 6f * t));

        // interpolate between start and max
        float startSize = proj.width();  // or store initial in a field on spawn
        float maxSize = (float) entity.getTrait("max_size", SizeTrait.class).getSize();
        float size = startSize + (maxSize - startSize) * ease;

        proj.setWidth(size);
        proj.setHeight(size);
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
