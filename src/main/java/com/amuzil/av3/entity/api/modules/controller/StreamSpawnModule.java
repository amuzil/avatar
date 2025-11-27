package com.amuzil.av3.entity.api.modules.controller;

import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IEntityModule;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.collision.FireModule;
import com.amuzil.av3.entity.api.modules.collision.SimpleDamageModule;
import com.amuzil.av3.entity.api.modules.collision.SimpleKnockbackModule;
import com.amuzil.av3.entity.api.modules.entity.GrowModule;
import com.amuzil.av3.entity.api.modules.entity.TimeResetModule;
import com.amuzil.av3.entity.api.modules.force.MoveModule;
import com.amuzil.av3.entity.construct.AvatarElementCollider;
import com.amuzil.av3.entity.controller.AvatarPhysicsController;
import com.amuzil.av3.utils.Constants;
import com.amuzil.caliber.physics.bullet.math.Convert;
import com.amuzil.magus.skill.traits.entitytraits.PointsTrait;
import com.amuzil.magus.skill.traits.skilltraits.*;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.amuzil.av3.utils.bending.SkillHelper.getLeftPivot;
import static com.amuzil.av3.utils.bending.SkillHelper.getRightPivot;

public class StreamSpawnModule implements IEntityModule {
    public static String id = StreamSpawnModule.class.getSimpleName();

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {

        // Only tick if physics is enabled
        if (entity.physics() && entity.owner() instanceof LivingEntity owner && entity instanceof AvatarPhysicsController controller) {
            Bender bender = Bender.getBender(owner);
            Level level = entity.level();
            // max should be a skill trait
            float maxPerTick = 0.5f;
            float secondsLoop = 1.5f;
            // And then we have to batch spawn them....
            // So we do this in the init phase, and then tick controls their movement
            PhysicsCollisionObject[] objs = new PhysicsCollisionObject[(int) (maxPerTick * 20 * secondsLoop)];
            // Default will be 1 entity per tick for 1.5 seconds with 20 ticks per second


            // Initialising constants/traits that are the same for all colliders here to reduce memory overhead

            float width = (float) entity.getTrait(Constants.SIZE, SizeTrait.class).getSize();
            float height = (float) entity.getTrait(Constants.SIZE, SizeTrait.class).getSize();

            SizeTrait maxSize = entity.getTrait(Constants.MAX_SIZE, SizeTrait.class);
            PointsTrait heightCurve = entity.getTrait(Constants.HEIGHT_CURVE, PointsTrait.class);
            PointsTrait widthCurve = entity.getTrait(Constants.WIDTH_CURVE, PointsTrait.class);

            DamageTrait damage = entity.getTrait(Constants.DAMAGE, DamageTrait.class);
            KnockbackTrait knockback = entity.getTrait(Constants.KNOCKBACK, KnockbackTrait.class);
            TimedTrait lifetime = entity.getTrait(Constants.COMPONENT_LIFE, TimedTrait.class);
            TimedTrait firetime = entity.getTrait(Constants.FIRE_TIME, TimedTrait.class);
            DirectionTrait direction = entity.getTrait(Constants.KNOCKBACK_DIRECTION, DirectionTrait.class);

            Vec3 origin = owner.getBoundingBox().getBottomCenter().add(0, (owner.getBoundingBox().maxY - owner.getBoundingBox().minY) / 2, 0);
            Vec3 pos;
            if (owner.getMainArm() == HumanoidArm.RIGHT) {
                pos = getRightPivot(owner, origin, 0.5f, 1.0f);
            } else {
                // Left arm
                pos = getLeftPivot(owner, origin, 0.5f, 1.0f);
            }

            for (int i = 0; i < maxPerTick * 20 * secondsLoop; i++) {

                // Spawn an element collider, then set its rigidbody properties.
                // Eventually, we want to have speical behaviour that moves the collider back to the controller,
                // rather than killing it (until the controller dies).
                AvatarElementCollider collider = new AvatarElementCollider(level);
                collider.setElement(entity.element());
//                Sound later
//                collider.setFX(skillData.getTrait(Constants.FX, StringTrait.class).getInfo());

                // Initial properties
                collider.setPos(pos);
                collider.setOwner(entity.owner());
                collider.spawner(controller);
                collider.setMaxLifetime(lifetime.getTime());

                // RigidBody
                collider.getRigidBody().setMass(0f);
                // We don't want this actually colliding with anything physical yet
                collider.getRigidBody().setKinematic(true);
                collider.getRigidBody().setGravity(Vector3f.ZERO);


                // Size should probably be set the same as regular flame (with a grow module)
                collider.setWidth(width);
                collider.setDepth(width);
                collider.setHeight(height);
                // Now we add the growth module and point curve properties
                collider.addTraits(maxSize);
                collider.addTraits(heightCurve);
                collider.addTraits(widthCurve);

                // Damage & Collision Module Properties
                collider.addTraits(damage);
                collider.addTraits(firetime);
                collider.addTraits(knockback);
                collider.addTraits(direction);

                // Motion Modules Properties

                // Behaviour Reset Module Properties
                collider.setMaxLifetime(lifetime.getTime());


                // Miscellaneous
                collider.setDamageable(false);
                collider.setControlled(true);
                collider.addModule(ModuleRegistry.create(MoveModule.id));
                // Init
                collider.init();


                // Add as a valid selection...
                bender.getSelection().addEntityId(collider.getUUID());
                // Spawn
                if (!entity.level().isClientSide)
                    entity.level().addFreshEntity(collider);

                // Add to collider list
                objs[i] = collider.getRigidBody();

                // Add to entity grid here
                controller.entityGrid().insert(collider);
            }


            for (PhysicsCollisionObject obj : objs) {
                // Innate != this check contained within the function so this is fine
                obj.setIgnoreList(objs);
            }
        }
    }

    @Override
    public void tick(AvatarEntity entity) {
        // Now want want to actually control the colliders here
        // Basically, enable their motion, and add relevant modules; choose 1 entity per tick to activate

//        System.out.println("Ticking Stream Spawn");
        if (!(entity instanceof AvatarPhysicsController controller && entity.getOwner() instanceof LivingEntity owner))
            return;

        controller.control(0f);
        Vec3 origin = owner.getBoundingBox().getBottomCenter().add(0, (owner.getBoundingBox().maxY - owner.getBoundingBox().minY) / 2, 0);
        Vec3 pos;
        if (owner.getMainArm() == HumanoidArm.RIGHT) {
            pos = getRightPivot(owner, origin, 0.5f, 1.0f);
        } else {
            // Left arm
            pos = getLeftPivot(owner, origin, 0.5f, 1.0f);
        }


        // Otherwise we want a fade out basically
        if (!controller.dying()) {
            // Shoot first
            List<AvatarElementCollider> toShoot = controller.entityGrid().allEntities();
            toShoot = toShoot.stream().filter(collider -> !collider.reset() && collider.getRigidBody().isKinematic()).toList();

            if (!toShoot.isEmpty()) {
                AvatarElementCollider next = toShoot.get(0);
                // Set physics
                next.getRigidBody().setKinematic(false);
                next.setControlled(false);
                next.getRigidBody().clearForces();
                next.getRigidBody().setMass(0.0f);

                // Add randomised lifetime?

                // Add relevant modules (properties defined in init)
                next.addModule(ModuleRegistry.create(SimpleDamageModule.id));
                next.addModule(ModuleRegistry.create(SimpleKnockbackModule.id));
                next.addModule(ModuleRegistry.create(FireModule.id));
                next.addModule(ModuleRegistry.create(TimeResetModule.id));
                next.addModule(ModuleRegistry.create(GrowModule.id));

                // Other shoot behaviours
                // Speed and randomness should sit on the controller rather then per entity being spawned
                next.shoot(pos, owner.getLookAngle(), controller.getTrait(Constants.SPEED, SpeedTrait.class).getSpeed() * 0.00005f,
                        controller.getTrait(Constants.RANDOMNESS, FloatTrait.class).getValue());
//                next.getRigidBody().applyCentralForce(Convert.toBullet(owner.getLookAngle().scale(0.5f)));
            }
            // Then reset
            List<AvatarElementCollider> colliders = controller.entityGrid().allEntities();
            for (AvatarElementCollider collider : colliders) {
                // Reset
                if (collider.reset()) {
                    collider.resetPhysics();
                    collider.resetPos(pos);
                    collider.reset(false);
                    collider.setControlled(true);
                    collider.tickCount = 0;
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
