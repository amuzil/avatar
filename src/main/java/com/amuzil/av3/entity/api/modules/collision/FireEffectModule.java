package com.amuzil.av3.entity.api.modules.collision;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.element.Elements;
import com.amuzil.av3.bending.form.BendingForm;
import com.amuzil.av3.bending.form.BendingForms;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.ICollisionModule;
import com.amuzil.av3.entity.projectile.AvatarProjectile;
import com.amuzil.av3.utils.Constants;
import com.amuzil.av3.utils.modules.HitDetection;
import com.amuzil.magus.skill.traits.skilltraits.CollisionTrait;
import com.amuzil.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.magus.skill.traits.skilltraits.StringTrait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FireEffectModule implements ICollisionModule {

    public static String id = FireEffectModule.class.getSimpleName();
    public static Map<Class<?>, EffectHandler> FIRE_EFFECT_HANDLERS = new HashMap<>();

    static {
        FIRE_EFFECT_HANDLERS.put(Blaze.class, (proj, entity, form, size) -> {
            if (!(entity instanceof Blaze otherEntity)) return;
            Vec3 direction = proj.getDeltaMovement();
            Vec3 pushVelocity = direction.scale(0.3);
            otherEntity.addDeltaMovement(pushVelocity);
            otherEntity.hurtMarked = true;
            otherEntity.hasImpulse = true;
        });

        FIRE_EFFECT_HANDLERS.put(Fireball.class, (proj, entity, form, size) -> {
            if (!(entity instanceof Fireball otherEntity)) return;
            Vec3 direction = proj.getDeltaMovement();
            Vec3 pushVelocity = direction.scale(1.0);
            otherEntity.addDeltaMovement(pushVelocity);
            otherEntity.hurtMarked = true;
            otherEntity.hasImpulse = true;
        });

        FIRE_EFFECT_HANDLERS.put(AvatarProjectile.class, (proj, entity, form, size) -> {
            if (!(entity instanceof AvatarProjectile otherEntity)) return;
            if (!proj.getOwner().equals(otherEntity.getOwner()) && entity.canBeHitByProjectile()) {
                if (otherEntity.element().equals(Elements.FIRE)) {
                    otherEntity.setOwner(proj.getOwner());
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
        if (formTrait == null || sizeTrait == null || collisions == null) {
            Avatar.LOGGER.warn("Either form, size or collision trait was not set for Collision module. Please remove the module or add the trait(s) to the entity.");
            return;
        }

        float size = (float) sizeTrait.getSize();
        BendingForm form = BendingForms.get(formTrait.name());

        for (Entity target: targets) {
            for (var entry: FIRE_EFFECT_HANDLERS.entrySet()) {
                if (entry.getKey().isInstance(target)) {
                    entry.getValue().handle((AvatarProjectile) entity, target, form, size);
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
