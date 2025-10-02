package com.amuzil.omegasource.entity.modules.collision;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.CollisionTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.DamageTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.projectile.AvatarProjectile;
import com.amuzil.omegasource.entity.api.ICollisionModule;
import com.amuzil.omegasource.entity.projectile.AirProjectile;
import com.amuzil.omegasource.entity.projectile.FireProjectile;
import com.amuzil.omegasource.entity.projectile.WaterProjectile;
import com.amuzil.omegasource.utils.Constants;
import com.amuzil.omegasource.utils.modules.HitDetection;
import com.lowdragmc.photon.client.fx.EntityEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.Fireball;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.amuzil.omegasource.Avatar.steam;


public class WaterCollisionModule implements ICollisionModule {

    public static String id = WaterCollisionModule.class.getSimpleName();
    public static Map<Class<?>, ProjectileHandler> WATER_PROJECTILE_HANDLERS = new HashMap<>();

    static {
        WATER_PROJECTILE_HANDLERS.put(Blaze.class, (proj, entity, damage, size) -> {
            entity.hurt(proj.damageSources().dragonBreath(), damage * 4f);
            EntityEffect entityEffect = new EntityEffect(steam, entity.level(), proj);
            entityEffect.start();
        });

        WATER_PROJECTILE_HANDLERS.put(Fireball.class, (proj, entity, damage, size) -> {
            if (!proj.getOwner().equals(((Fireball) entity).getOwner())) {
                entity.discard();
            }
        });

        WATER_PROJECTILE_HANDLERS.put(AirProjectile.class, (proj, entity, damage, size) -> {
            if (!proj.getOwner().equals(((AvatarProjectile) entity).getOwner())) {
                proj.discard();
            }
        });

        WATER_PROJECTILE_HANDLERS.put(FireProjectile.class, (proj, entity, damage, size) -> {
            if (!proj.getOwner().equals(((AvatarProjectile) entity).getOwner())) {
                proj.discard();
            }
        });

        WATER_PROJECTILE_HANDLERS.put(WaterProjectile.class, (proj, entity, damage, size) -> {
            if (!proj.getOwner().equals(((AvatarProjectile) entity).getOwner())) {
                proj.discard();
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
        CollisionTrait collisions = entity.getTrait(Constants.COLLISION_TYPE, CollisionTrait.class);
        if (damageTrait == null || sizeTrait == null || collisions == null) {
            Avatar.LOGGER.warn("Either damage, size or collision trait was not set for Collision module. Please remove the module or add the trait(s) to the entity.");
            return;
        }
        if (targets.isEmpty())
            return;
        float damage = (float) damageTrait.getDamage();
        float size = (float) sizeTrait.getSize();

        for (Entity target: targets) {
            for (var entry: WATER_PROJECTILE_HANDLERS.entrySet()) {
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
