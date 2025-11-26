package com.amuzil.av3.entity.api.modules.controller;

import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IEntityModule;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.entity.GrowModule;
import com.amuzil.av3.entity.construct.AvatarElementCollider;
import com.amuzil.av3.utils.Constants;
import com.amuzil.av3.utils.maths.Easings;
import com.amuzil.magus.skill.traits.entitytraits.PointsTrait;
import com.amuzil.magus.skill.traits.skilltraits.SizeTrait;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
            int maxPerTick = 1;
            int secondsLoop = 3;
            // And then we have to batch spawn them....
            // So we do this in the init phase, and then tick controls their movement
            PhysicsCollisionObject[] objs = new PhysicsCollisionObject[maxPerTick * 20 * secondsLoop];
            // Default will be 1 entity per tick for 3 seconds with 20 ticks per second


            // Initialising constants/traits that are the same for all colliders here to reduce memory overhead

            float width = (float) entity.getTrait(Constants.SIZE, SizeTrait.class).getSize();
            float height = (float) entity.getTrait(Constants.SIZE, SizeTrait.class).getSize();

            SizeTrait maxSize = entity.getTrait(Constants.MAX_SIZE, SizeTrait.class);
            PointsTrait heightCurve = new PointsTrait(Constants.HEIGHT_CURVE, Easings.FIRE_CURVE_HEIGHT);
            PointsTrait widthCurve = new PointsTrait(Constants.WIDTH_CURVE, Easings.FIRE_CURVE_WIDTH);

            Vec3 origin = entity.getBoundingBox().getBottomCenter().add(0, (entity.getBoundingBox().maxY - entity.getBoundingBox().minY) / 2, 0);
            Vec3 pos = getRightPivot(entity, origin, 0.5f, 1.0f);

            for (int i = 0; i < maxPerTick * 20 * secondsLoop; i++) {

                // Spawn an element collider, then set its rigidbody properties.
                // Eventually, we want to have speical behaviour that moves the collider back to the controller,
                // rather than killing it (until the controller dies).
                AvatarElementCollider collider = new AvatarElementCollider(level);
                collider.setElement(entity.element());
//                Sound later
//                collider.setFX(skillData.getTrait(Constants.FX, StringTrait.class).getInfo());
                collider.setPos(pos);
                collider.getRigidBody().setMass(0f);
                collider.getRigidBody().setKinematic(false);
                collider.setOwner(entity.owner());
                collider.setMaxLifetime(entity.maxLifetime());

                // Size should probably be set the same as regular flame (with a grow module)
                collider.setWidth(width);
                collider.setHeight(height);
                // Now we add the growth module and point curves
                collider.addTraits(maxSize);
                collider.addTraits(heightCurve);
                collider.addTraits(widthCurve);
                entity.addModule(ModuleRegistry.create(GrowModule.id));



                collider.getRigidBody().setGravity(Vector3f.ZERO);
                collider.setDamageable(false);
                collider.setControlled(true);

                collider.init();
                bender.getSelection().addEntityId(collider.getUUID());
                if (!entity.level().isClientSide)
                    entity.level().addFreshEntity(collider);
                
                objs[i] = collider.getRigidBody();
            }

            for (PhysicsCollisionObject obj : objs) {
                // Innate != this check contained within the function so this is fine
                obj.setIgnoreList(objs);
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
