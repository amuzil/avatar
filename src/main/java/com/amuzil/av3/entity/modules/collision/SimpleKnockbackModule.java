package com.amuzil.av3.entity.modules.collision;

import com.amuzil.magus.skill.traits.skilltraits.DirectionTrait;
import com.amuzil.magus.skill.traits.skilltraits.KnockbackTrait;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.ICollisionModule;
import com.amuzil.av3.utils.Constants;
import com.amuzil.av3.utils.modules.HitDetection;
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

    public static String id = SimpleKnockbackModule.class.getSimpleName();

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
        List<LivingEntity> targets = HitDetection.getEntitiesWithinBox(entity, 0.75f,
                hit -> hit != entity.owner() && (!(hit instanceof AvatarEntity) || ((AvatarEntity) hit).owner() != entity.owner()),
                LivingEntity.class);

        Vec3 knockback = entity.getTrait(Constants.KNOCKBACK_DIRECTION, DirectionTrait.class).direction();
        float scale = (float) entity.getTrait(Constants.KNOCKBACK, KnockbackTrait.class).getKnockback();
        Vec3 motion = entity.getDeltaMovement();

        // TODO: make projectile entities not hit the same target multiple times
        for (LivingEntity hit : targets) {
            hit.addDeltaMovement(motion.add(knockback.scale(scale)));
            hit.hurtMarked = true;
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
