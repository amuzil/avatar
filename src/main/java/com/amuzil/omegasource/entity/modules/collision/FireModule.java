package com.amuzil.omegasource.entity.modules.collision;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.TimedTrait;
import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.modules.ICollisionModule;
import com.amuzil.omegasource.utils.HitDetection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

// Sets an entity on fire.
public class FireModule implements ICollisionModule {
    String name = "fire";
    @Override
    public String id() {
        return name;
    }

    @Override
    public void init(AvatarEntity entity) {

    }

    @Override
    public void tick(AvatarEntity entity) {
        int firetime = -1;
        TimedTrait fire = entity.getTrait("firetime", TimedTrait.class);
        if (fire != null) {
            Avatar.LOGGER.warn("No fire time trait set for fire module. Remove the module or add the trait to the entity.");
            firetime = fire.getTime();
        }

        if (firetime > -1) {
            List<LivingEntity> targets = HitDetection.getEntitiesWithinBox(entity, 0.75f, hit ->
                            hit != entity.owner() && !hit.fireImmune() && (!(hit instanceof AvatarEntity) || ((AvatarEntity) hit).owner() != entity.owner())
                    , LivingEntity.class);

            for (LivingEntity hit : targets) {
                hit.setRemainingFireTicks(hit.getRemainingFireTicks() + firetime);
            }
        }
    }

    @Override
    public void save(CompoundTag nbt) {

    }

    @Override
    public void load(CompoundTag nbt) {

    }
}
