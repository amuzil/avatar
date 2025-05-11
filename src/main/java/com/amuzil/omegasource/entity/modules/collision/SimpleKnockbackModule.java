package com.amuzil.omegasource.entity.modules.collision;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.DirectionTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.KnockbackTrait;
import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.modules.ICollisionModule;
import com.amuzil.omegasource.utils.HitDetection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Very basic knockback module. Only targets valid living entities.
 * No bending interaction (with own or other entities' bending), no projectile interaction, e.t.c.
 * Provides knockback based on the entity's current velocity, scales, it, and adds an extra scaled vector.
 */
public class SimpleKnockbackModule implements ICollisionModule {

    String id = "simple_knockback";
    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {

    }

    @Override
    public void tick(AvatarEntity entity) {
        // 1.25f gives a nice, extra boost
        List<LivingEntity> targets = HitDetection.getEntitiesWithinBox(entity, 0.75f, hit ->
            hit != entity.owner() && (!(hit instanceof AvatarEntity) || ((AvatarEntity) hit).owner() != entity.owner())
        , LivingEntity.class);

//        Avatar.LOGGER.debug("Knockback ticking");
        Vec3 knockback = entity.getTrait("knockback_direction", DirectionTrait.class).direction();
        float scale = (float) entity.getTrait("knockback", KnockbackTrait.class).getKnockback();
        Vec3 motion = entity.getDeltaMovement();

        // TODO: make projectile entities not hit the same target multiple times
        for (LivingEntity hit : targets) {
            hit.addDeltaMovement(motion.scale(scale).add(knockback.scale(scale)));
            hit.hasImpulse = true;
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
