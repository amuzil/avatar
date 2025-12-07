package com.amuzil.av3.entity.api.modules.collision;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.element.Element;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.ICollisionModule;
import com.amuzil.av3.entity.construct.AvatarConstruct;
import com.amuzil.av3.entity.projectile.AvatarProjectile;
import com.amuzil.av3.utils.Constants;
import com.amuzil.av3.utils.modules.HitDetection;
import com.amuzil.magus.skill.traits.skilltraits.CollisionTrait;
import com.amuzil.magus.skill.traits.skilltraits.DamageTrait;
import com.amuzil.magus.skill.traits.skilltraits.SizeTrait;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.IronGolem;
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


public class EarthCollisionModule implements ICollisionModule {

    public static String id = EarthCollisionModule.class.getSimpleName();
    public static Map<Class<?>, ConstructHandler> EARTH_CONSTRUCT_HANDLERS = new HashMap<>();

    static {
        EARTH_CONSTRUCT_HANDLERS.put(IronGolem.class, (construct, entity, damage, size) -> {
            Entity owner = construct.getOwner();
            if (owner != null) {
                construct.setOwner(entity);
                Vec3 dir = entity.getViewVector(1);
                entity.hurt(entity.damageSources().cramming(), damage);
            }
        });

        EARTH_CONSTRUCT_HANDLERS.put(Fireball.class, (construct, entity, damage, size) -> {
            if (!construct.getOwner().equals(((Fireball) entity).getOwner())) {
                entity.discard();
            }
        });

        EARTH_CONSTRUCT_HANDLERS.put(AbstractArrow.class, (construct, entity, damage, size) -> {
            if (!construct.getOwner().equals(((AbstractArrow) entity).getOwner())) {
                entity.discard();
            }
        });

        EARTH_CONSTRUCT_HANDLERS.put(AvatarProjectile.class, (construct, entity, damage, size) -> {
            if (!construct.getOwner().equals(((AvatarProjectile) entity).getOwner()) && entity.canBeHitByProjectile()) {
//                Element element = ((AvatarProjectile) entity).element();
//                switch (element.type()) {
//                    case AIR, EARTH, FIRE, WATER -> entity.discard();
//                    default -> entity.hurt(construct.damageSources().cramming(), damage);
//                }
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

        if (targets.isEmpty()) {
            Vec3 pos = entity.position();
            Vec3 delta = pos.add(entity.getDeltaMovement());
            BlockHitResult hitResult = entity.level().clip(new ClipContext(pos, delta, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                // TODO: Play FX when collided with block
                BlockPos blockpos = hitResult.getBlockPos();
                entity.level().gameEvent(GameEvent.PROJECTILE_LAND, blockpos, GameEvent.Context.of(entity, entity.level().getBlockState(blockpos)));
            }
        }

        float damage = (float) damageTrait.getDamage();
        float size = (float) sizeTrait.getSize();

        for (Entity target: targets) {
            for (var entry: EARTH_CONSTRUCT_HANDLERS.entrySet()) {
                if (entry.getKey().isInstance(target)) {
                    entry.getValue().handle((AvatarConstruct) entity, target, damage, size);
                    return;
                }
            }
            target.hurt(entity.damageSources().cramming(), damage);
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
