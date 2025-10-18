package com.amuzil.omegasource.entity.modules.collision;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.CollisionTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.DamageTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.StringTrait;
import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.api.ICollisionModule;
import com.amuzil.omegasource.entity.projectile.AvatarProjectile;
import com.amuzil.omegasource.utils.Constants;
import com.amuzil.omegasource.utils.modules.HitDetection;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FireEffectModule implements ICollisionModule {

    public static String id = FireEffectModule.class.getSimpleName();
    public static Map<Class<?>, ProjectileHandler> FIRE_PROJECTILE_HANDLERS = new HashMap<>();

    static {
        FIRE_PROJECTILE_HANDLERS.put(Blaze.class, (proj, entity, damage, size) -> {
            if (!(entity instanceof Blaze otherEntity)) return;
            Vec3 direction = proj.getDeltaMovement();
            Vec3 pushVelocity = direction.scale(0.3);
            otherEntity.addDeltaMovement(pushVelocity);
            otherEntity.hurtMarked = true;
            otherEntity.hasImpulse = true;
        });

        FIRE_PROJECTILE_HANDLERS.put(Fireball.class, (proj, entity, damage, size) -> {
            if (!(entity instanceof Fireball otherEntity)) return;
            Vec3 direction = proj.getDeltaMovement();
            Vec3 pushVelocity = direction.scale(1.0);
            otherEntity.setDeltaMovement(otherEntity.getDeltaMovement().add(pushVelocity));
            otherEntity.hurtMarked = true;
            otherEntity.hasImpulse = true;
        });

        FIRE_PROJECTILE_HANDLERS.put(AvatarProjectile.class, (proj, entity, damage, size) -> {
            if (!(entity instanceof AvatarProjectile otherEntity)) return;
            if (!proj.getOwner().equals(otherEntity.getOwner()) && entity.canBeHitByProjectile()) {
                if (otherEntity.element().equals(Elements.FIRE)) {
                    Vec3 direction = proj.getDeltaMovement();
//                    Vec3 direction = otherEntity.position().subtract(proj.position()).normalize();
                    Vec3 pushVelocity = direction.scale(1.0);
                    otherEntity.addDeltaMovement(pushVelocity);
                    otherEntity.hurtMarked = true;
                    otherEntity.hasImpulse = true;
                }
            }
        });

    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {

    }

    @Override
    public void tick(AvatarEntity entity) {
        List<Entity> targets = HitDetection.getEntitiesWithinBox(entity, 0.75f,
                hit -> hit != entity.owner() && (!(hit instanceof AvatarEntity) || ((AvatarEntity) hit).owner() != entity.owner()),
                Entity.class);
        SizeTrait sizeTrait = entity.getTrait(Constants.SIZE, SizeTrait.class);
        CollisionTrait collisions = entity.getTrait(Constants.COLLISION_TYPE, CollisionTrait.class);
        StringTrait formTrait = entity.getTrait(Constants.BENDING_FORM, StringTrait.class);
        if (sizeTrait == null || collisions == null) {
            Avatar.LOGGER.warn("Either damage, size or collision trait was not set for Collision module. Please remove the module or add the trait(s) to the entity.");
            return;
        }

        float size = (float) sizeTrait.getSize();

        for (Entity target: targets) {
            for (var entry: FIRE_PROJECTILE_HANDLERS.entrySet()) {
                if (entry.getKey().isInstance(target)) {
                    entry.getValue().handle((AvatarProjectile) entity, target, 0, size);
                }
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
