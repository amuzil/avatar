package com.amuzil.omegasource.entity.modules.collision;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.DamageTrait;
import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.AvatarProjectile;
import com.amuzil.omegasource.entity.api.ICollisionModule;
import com.amuzil.omegasource.entity.projectile.FireProjectile;
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

    String id = "fire_collision";

    static {
        PROJECTILE_HANDLERS.put(Blaze.class, (proj, entity) -> {
            Entity owner = proj.getOwner();
            if (owner != null) {
                proj.setOwner(entity);
                Vec3 dir = entity.getViewVector(1);
                proj.shoot(dir.x, dir.y, dir.z, 0.75F, 0);
                proj.leftOwner = true;
            }
        });

        PROJECTILE_HANDLERS.put(Fireball.class, (proj, entity) -> {
            if (!proj.getOwner().equals(((Fireball) entity).getOwner())) {
                entity.discard();
            }
        });

        PROJECTILE_HANDLERS.put(AbstractArrow.class, (proj, entity) -> {
            if (!proj.getOwner().equals(((AbstractArrow) entity).getOwner())) {
                entity.discard();
            }
        });

        registerProjectileHandler(FireProjectile.class, (proj, entity) -> {
            if (!proj.getOwner().equals(((AvatarProjectile) entity).getOwner())) {
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
        DamageTrait dmg = entity.getTrait("damage", DamageTrait.class);
        if (dmg == null) {
            Avatar.LOGGER.warn("No damage trait set for SimpleDamage module. Please remove the module or add the trait to the entity.");
            return;
        }
        if (targets.isEmpty())
            return;
        float damage = (float) dmg.getDamage();

        for (Entity target : targets) {
            for (var entry: PROJECTILE_HANDLERS.entrySet()) {
                if (entry.getKey().isInstance(target)) {
                    entry.getValue().handle((AvatarProjectile) entity, target);
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
