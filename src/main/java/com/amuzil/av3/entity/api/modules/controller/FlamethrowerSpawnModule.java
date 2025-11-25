package com.amuzil.av3.entity.api.modules.controller;

import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IEntityModule;
import com.amuzil.av3.entity.construct.AvatarElementCollider;
import com.jme3.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import static com.amuzil.av3.utils.bending.SkillHelper.getRightPivot;

public class FlamethrowerSpawnModule implements IEntityModule {
    @Override
    public String id() {
        return "";
    }

    @Override
    public void init(AvatarEntity entity) {

        // Only tick if physics is enabled
        if (entity.physics() && entity.owner() instanceof LivingEntity owner) {
            Bender bender = Bender.getBender(owner);
            Level level = entity.level();
            // max should be a skill trait
            int max = 1;
            for (int i = 0; i < max; i++) {
                AvatarElementCollider collider = new AvatarElementCollider(level);
                collider.setElement(entity.element());
//                Sound later
//                collider.setFX(skillData.getTrait(Constants.FX, StringTrait.class).getInfo());
                collider.setPos(getRightPivot(entity, 1.0f));
                collider.getRigidBody().setMass(0f);
                collider.getRigidBody().setKinematic(false);
                collider.setOwner(entity.owner());
                collider.setMaxLifetime(entity.maxLifetime());
                collider.setWidth((float) size);
                collider.setHeight((float) size);
                collider.getRigidBody().setGravity(Vector3f.ZERO);
                collider.setDamageable(false);
                collider.setControlled(true);

                collider.init();
                bender.getSelection().addEntityId(collider.getUUID());
                if (!entity.level().isClientSide)
                    entity.level().addFreshEntity(collider);

                collider.shoot();
            }
        }
    }

    @Override
    public void tick(AvatarEntity entity) {

    }

    @Override
    public void save(CompoundTag nbt) {

    }

    @Override
    public void load(CompoundTag nbt) {

    }
}
