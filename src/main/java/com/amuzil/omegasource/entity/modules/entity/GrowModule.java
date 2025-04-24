package com.amuzil.omegasource.entity.modules.entity;

import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.AvatarProjectile;
import com.amuzil.omegasource.entity.modules.IEntityModule;
import com.amuzil.omegasource.utils.Easings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
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
        float t = (float) age / (float) (life);
        t = Mth.clamp(t, 0f, 1f);

        // Compute overall growth (quintic or other)
        float startSize = proj.width();
        float maxSize   = (float)entity.getTrait("max_size", SizeTrait.class).getSize();
        float overall   = startSize + (maxSize - startSize) * Easings.quinticEaseInOut(t);

        // Bezier parameters for width: mid-life peak at ~1.4×
        float p1x = 0.45f, p1y = 2.5f;
        float p2x = 0.99f, p2y = -0.5f;
        // Evaluate cubic-Bezier width factor
        float widthFactor = Easings.cubicBezier(t, p1x, p1y, p2x, p2y);

        p1x = 0.15f;
        p1y = 1.4f;
        p2x = 0.85f;
        p2y = 0.95f;
        // Evaluate cubic-Bezier width factor
        float heightFactor = Easings.cubicBezier(t, p1x, p1y, p2x, p2y);

        // Apply final scales
        proj.setWidth(overall * widthFactor);
        proj.setHeight(overall * heightFactor);
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
