package com.amuzil.av3.entity.modules.collision;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.api.magus.skill.traits.skilltraits.TimedTrait;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.ICollisionModule;
import com.amuzil.av3.utils.modules.HitDetection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

// Sets an entity on fire.
public class FireModule implements ICollisionModule {

    public static String id = FireModule.class.getSimpleName();

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {

    }

    @Override
    public void tick(AvatarEntity entity) {
        int firetime = -1;
        TimedTrait fire = entity.getTrait("fire_time", TimedTrait.class);
        if (fire == null) {
            Avatar.LOGGER.warn("No fire time trait set for fire module. Remove the module or add the trait to the entity.");
            return;
        }
        firetime = fire.getTime();

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
        nbt.putString("ID", id);
    }

    @Override
    public void load(CompoundTag nbt) {
        id = nbt.getString("ID");
    }
}
