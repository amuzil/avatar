package com.amuzil.omegasource.entity.modules.collision;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.CollisionTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.DamageTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.AvatarProjectile;
import com.amuzil.omegasource.entity.api.ICollisionModule;
import com.amuzil.omegasource.entity.projectile.FireProjectile;
import com.amuzil.omegasource.entity.projectile.WaterProjectile;
import com.amuzil.omegasource.utils.Constants;
import com.amuzil.omegasource.utils.modules.HitDetection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.amuzil.omegasource.entity.api.ICollisionModule.*;


public class FireCollisionModule implements ICollisionModule {

    public static String id = FireCollisionModule.class.getSimpleName();

    static {
        PROJECTILE_HANDLERS.put(Blaze.class, (proj, entity, damage, size) -> {
            Entity owner = proj.getOwner();
            if (owner != null) {
                proj.setOwner(entity);
                Vec3 dir = entity.getViewVector(1);
                proj.shoot(dir.x, dir.y, dir.z, 0.75F, 0);
                proj.leftOwner = true;
            }
        });

        PROJECTILE_HANDLERS.put(Fireball.class, (proj, entity, damage, size) -> {
            if (!proj.getOwner().equals(((Fireball) entity).getOwner())) {
                entity.discard();
            }
        });

        PROJECTILE_HANDLERS.put(AbstractArrow.class, (proj, entity, damage, size) -> {
            if (!proj.getOwner().equals(((AbstractArrow) entity).getOwner())) {
                entity.discard();
            }
        });

        registerProjectileHandler(FireProjectile.class, (proj, entity, damage, size) -> {
            if (!proj.getOwner().equals(((AvatarProjectile) entity).getOwner())) {
                entity.discard();
            }
        });

        registerProjectileHandler(WaterProjectile.class, (proj, entity, damage, size) -> {
            if (!proj.getOwner().equals(((AvatarProjectile) entity).getOwner())) {
                proj.discard();
                entity.discard();
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
        DamageTrait damageTrait = entity.getTrait(Constants.DAMAGE, DamageTrait.class);
        SizeTrait sizeTrait = entity.getTrait(Constants.SIZE, SizeTrait.class);
        CollisionTrait collisions = entity.getTrait("collision", CollisionTrait.class);
        if (damageTrait == null || sizeTrait == null || collisions == null) {
            Avatar.LOGGER.warn("Either damage, size or collision trait was not set for Collision module. Please remove the module or add the trait(s) to the entity.");
            return;
        }
        if (targets.isEmpty())
            return;
        float damage = (float) damageTrait.getDamage();
        float size = (float) sizeTrait.getSize();

        for (Entity target: targets) {
            for (var entry: PROJECTILE_HANDLERS.entrySet()) {
//                ProjectileHandler handler = PROJECTILE_HANDLERS.get(collision);
                if (entry.getKey().isInstance(target)) {
                    entry.getValue().handle((AvatarProjectile) entity, target, damage, size);
                    return;
                }
            }
            target.hurt(entity.damageSources().dragonBreath(), damage);
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
