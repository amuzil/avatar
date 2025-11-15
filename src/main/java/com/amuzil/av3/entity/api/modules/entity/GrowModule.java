package com.amuzil.av3.entity.api.modules.entity;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IEntityModule;
import com.amuzil.av3.entity.projectile.AvatarProjectile;
import com.amuzil.av3.utils.Constants;
import com.amuzil.magus.skill.traits.entitytraits.PointsTrait;
import com.amuzil.magus.skill.traits.skilltraits.SizeTrait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class GrowModule implements IEntityModule {

    public static String id = GrowModule.class.getSimpleName();

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
        SizeTrait maxTrait = entity.getTrait(Constants.MAX_SIZE, SizeTrait.class);
        if (maxTrait == null) {
            Avatar.LOGGER.warn("No max size set for growth module. Remove the module or set max size");
            return;
        }
        float maxSize = (float) maxTrait.getSize();
        float overall = startSize + (maxSize - startSize) * t;

        // two different ways to grow.
        PointsTrait sizePoints = entity.getTrait("size_curve", PointsTrait.class);
        if (sizePoints != null) {
            float sizeFactor = sizePoints.evaluate(t);
            float widthFactor = (float) entity.getTrait("width_factor", SizeTrait.class).getSize();
            float heightFactor = (float) entity.getTrait("height_factor", SizeTrait.class).getSize();

            proj.setWidth(overall * widthFactor * sizeFactor);
            proj.setHeight(overall * heightFactor * sizeFactor);
        } else {
            PointsTrait widthPoints = entity.getTrait("width_curve", PointsTrait.class);
            PointsTrait heightPoints = entity.getTrait("height_curve", PointsTrait.class);


            float widthFactor = widthPoints.evaluate(t);
            // Evaluate cubic-Bezier width factor
            float heightFactor = heightPoints.evaluate(t);

            // Apply final scales
            proj.setWidth(overall * widthFactor);
            proj.setHeight(overall * heightFactor);
        }
    }

    @Override
    public void save(CompoundTag nbt) {
        nbt.putString("ID", id);
    }

    @Override
    public void load(CompoundTag nbt) {
        id = nbt.getString("ID");
    }
}
