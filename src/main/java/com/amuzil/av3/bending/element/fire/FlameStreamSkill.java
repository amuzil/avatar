package com.amuzil.av3.bending.element.fire;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.form.BendingForms;
import com.amuzil.av3.bending.skill.FireSkill;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.entity.api.ICollisionModule;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.collision.FireCollisionModule;
import com.amuzil.av3.entity.api.modules.collision.FireModule;
import com.amuzil.av3.entity.api.modules.collision.SimpleKnockbackModule;
import com.amuzil.av3.entity.api.modules.controller.StreamSpawnModule;
import com.amuzil.av3.entity.api.modules.entity.GrowModule;
import com.amuzil.av3.entity.api.modules.force.ChangeSpeedModule;
import com.amuzil.av3.entity.api.modules.force.MoveModule;
import com.amuzil.av3.entity.controller.AvatarPhysicsController;
import com.amuzil.av3.entity.projectile.AvatarDirectProjectile;
import com.amuzil.av3.utils.Constants;
import com.amuzil.av3.utils.maths.Easings;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.traits.entitytraits.PointsTrait;
import com.amuzil.magus.skill.traits.skilltraits.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


public class FlameStreamSkill { //extends FireSkill {
//
//    public FlameStreamSkill() {
//        super(Avatar.MOD_ID, "flame_stream");
//        addTrait(new DamageTrait(Constants.DAMAGE, 1.5f));
//        addTrait(new SizeTrait(Constants.SIZE, 0.125F));
//        addTrait(new SizeTrait(Constants.MAX_SIZE, 1.0f));
//        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 0.012f));
//        addTrait(new ColourTrait(0, 0, 0, Constants.FIRE_COLOUR));
//        addTrait(new SpeedTrait(Constants.SPEED, 0.875d));
//        addTrait(new TimedTrait(Constants.LIFETIME, 40));
//        addTrait(new TimedTrait(Constants.COMPONENT_LIFE, 40));
//        addTrait(new TimedTrait(Constants.FIRE_TIME, 2));
//        addTrait(new SpeedTrait(Constants.SPEED_FACTOR, 0.85d));
//        addTrait(new StringTrait(Constants.FX, "flamethrower"));
//
//        startPaths = SkillPathBuilder.getInstance()
////                .simple(new ActiveForm(BendingForms.ARC, true))
////                .add(BendingForms.EXPAND)
//                .add(BendingForms.BLOCK)
//                .build();
//
//        stopPaths = SkillPathBuilder.getInstance().build();
//    }
//
//    @Override
//    public void start(Bender bender) {
//
//        LivingEntity entity = bender.getEntity();
//        Level level = bender.getEntity().level();
//
//        int lifetime = skillData.getTrait(Constants.LIFETIME, TimedTrait.class).getTime();
//        double speed = skillData.getTrait(Constants.SPEED, SpeedTrait.class).getSpeed();
//        double size = skillData.getTrait(Constants.SIZE, SizeTrait.class).getSize();
//
//        LivingEntity owner = bender.getEntity();
//        Vec3 pos = owner.position();
//        for (int i = 0; i < 1; i++) {
////            AvatarPhysicsController flameManager = new AvatarPhysicsController(level);
////            flameManager.setElement(element());
////            flameManager.setFX(skillData.getTrait(Constants.FX, StringTrait.class).getInfo());
////            flameManager.setOwner(entity);
////            flameManager.setMaxLifetime(lifetime);
////            flameManager.setWidth((float) size);
////            flameManager.setDepth((float) size);
////            flameManager.setHeight((float) size);
////            flameManager.setNoGravity(true);
////            flameManager.setDamageable(false);
////            flameManager.controlled(true);
////            flameManager.setPos(pos);
////            flameManager.addTraits(skillData.getTrait(Constants.MAX_SIZE, SizeTrait.class));
////            flameManager.addTraits(skillData.getTrait(Constants.DAMAGE, DamageTrait.class));
////            flameManager.addTraits(skillData.getTrait(Constants.SIZE, SizeTrait.class));
////            flameManager.addTraits(skillData.getTrait(Constants.KNOCKBACK, KnockbackTrait.class));
////            flameManager.addTraits(new DirectionTrait(Constants.KNOCKBACK_DIRECTION, new Vec3(0, 0.45, 0)));
////            flameManager.addTraits(skillData.getTrait(Constants.FIRE_TIME, TimedTrait.class));
////            flameManager.addTraits(skillData.getTrait(Constants.SPEED, SpeedTrait.class));
////            flameManager.addTraits(skillData.getTrait(Constants.COMPONENT_LIFE, TimedTrait.class));
////            flameManager.addTraits(new PointsTrait(Constants.HEIGHT_CURVE, Easings.FIRE_CURVE_HEIGHT));
////            flameManager.addTraits(new PointsTrait(Constants.WIDTH_CURVE, Easings.FIRE_CURVE_WIDTH));
////            flameManager.addTraits(new FloatTrait(Constants.RANDOMNESS, 0.1f));
////            flameManager.addModule(ModuleRegistry.create(StreamSpawnModule.id));
////            flameManager.setPhysics(true);
////            flameManager.addModule(ModuleRegistry.create(MoveModule.id));
//
////        AvatarDirectProjectile projectile = new AvatarDirectProjectile(level);
////        projectile.setElement(element());
////        projectile.setFX(skillData.getTrait(Constants.FX, StringTrait.class).getInfo());
////        projectile.setOwner(entity);
////        projectile.setMaxLifetime(lifetime);
////        projectile.setWidth((float) size);
////        projectile.setHeight((float) size);
////        projectile.setNoGravity(true);
////        projectile.setDamageable(false);
////
////        projectile.addTraits(skillData.getTrait(Constants.MAX_SIZE, SizeTrait.class));
////
////        // Copied from the fire easing constant
////        projectile.addTraits(new PointsTrait("height_curve", new Point(0.00, 0.5),  // t=0: zero width
////                new Point(0.20, 0.75),  // rise slowly
////                new Point(0.40, 2.5),  // flare to 150%
////                new Point(0.70, 0.40),  // rapid taper
////                new Point(1.00, 0.00)   // die out completely
////        ));
////
////        // Used for bezier curving
////        projectile.addTraits(new PointsTrait("width_curve", new Point(0.00, 0.5),  // t=0: zero width
////                new Point(0.20, 0.75),  // rise slowly
////                new Point(0.40, 1.75),  // flare to 150%
////                new Point(0.70, 0.40),  // rapid taper
////                new Point(1.00, 0.00)   // die out completely
////        ));
////
////        projectile.addModule(ModuleRegistry.create(GrowModule.id));
////
////        projectile.addTraits(skillData.getTrait(Constants.KNOCKBACK, KnockbackTrait.class));
////        projectile.addTraits(new DirectionTrait(Constants.KNOCKBACK_DIRECTION, new Vec3(0, 0.45, 0)));
////        projectile.addModule(ModuleRegistry.create(SimpleKnockbackModule.id));
////
////        // Set Fire module
////        projectile.addTraits(skillData.getTrait(Constants.FIRE_TIME, TimedTrait.class));
////        projectile.addModule(ModuleRegistry.create(FireModule.id));
////
////        // Damage module
////        projectile.addTraits(skillData.getTrait(Constants.DAMAGE, DamageTrait.class));
////        projectile.addTraits(skillData.getTrait(Constants.SIZE, SizeTrait.class));
////        projectile.addTraits(new CollisionTrait(Constants.COLLISION_TYPE, "Blaze", "Fireball", "AbstractArrow", "FireProjectile"));
////        projectile.addCollisionModule((ICollisionModule) ModuleRegistry.create(FireCollisionModule.id));
////
////        // Slow down over time
////        projectile.addTraits(skillData.getTrait(Constants.SPEED_FACTOR, SpeedTrait.class));
////        projectile.addModule(ModuleRegistry.create(ChangeSpeedModule.id));
////
////        // Particle FX
////        projectile.shoot(entity.position().add(0, entity.getEyeHeight(), 0), entity.getLookAngle(), speed, 0);
////        projectile.init();
////
////            flameManager.init();
////            bender.getEntity().level().addFreshEntity(flameManager);
//
//            // Spawn AvatarPhysicsController to handle continuous firing
//            // Set modules on the controller as needed
//        }
//        super.startRun();
//    }
//
//    @Override
//    public void run(Bender bender) {
//        super.run(bender);
//        // TODO: Limit rate of fire and how many entities play sound
//        LivingEntity entity = bender.getEntity();
//        Level level = bender.getEntity().level();
//
//        int lifetime = skillData.getTrait(Constants.LIFETIME, TimedTrait.class).getTime();
//        double speed = skillData.getTrait(Constants.SPEED, SpeedTrait.class).getSpeed();
//        double size = skillData.getTrait(Constants.SIZE, SizeTrait.class).getSize();
//
//        AvatarDirectProjectile projectile = new AvatarDirectProjectile(level);
//        projectile.setElement(element());
//        projectile.setFX(skillData.getTrait(Constants.FX, StringTrait.class).getInfo());
//        projectile.setOwner(entity);
//        projectile.setMaxLifetime(lifetime);
//        projectile.setWidth((float) size);
//        projectile.setHeight((float) size);
//        projectile.setNoGravity(true);
//        projectile.setDamageable(false);
//
//        projectile.addTraits(skillData.getTrait(Constants.MAX_SIZE, SizeTrait.class));
//
//        // Copied from the fire easing constant
//        projectile.addTraits(new PointsTrait("height_curve", Easings.FIRE_CURVE_HEIGHT));
//
//        // Used for bezier curving
//        projectile.addTraits(new PointsTrait("width_curve", Easings.FIRE_CURVE_WIDTH));
//
//        projectile.addModule(ModuleRegistry.create(GrowModule.id));
//
//        projectile.addTraits(skillData.getTrait(Constants.KNOCKBACK, KnockbackTrait.class));
//        projectile.addTraits(new DirectionTrait(Constants.KNOCKBACK_DIRECTION, new Vec3(0, 0.45, 0)));
//        projectile.addModule(ModuleRegistry.create(SimpleKnockbackModule.id));
//
//        // Set Fire module
//        projectile.addTraits(skillData.getTrait(Constants.FIRE_TIME, TimedTrait.class));
//        projectile.addModule(ModuleRegistry.create(FireModule.id));
//
//        // Damage module
//        projectile.addTraits(skillData.getTrait(Constants.DAMAGE, DamageTrait.class));
//        projectile.addTraits(skillData.getTrait(Constants.SIZE, SizeTrait.class));
////        projectile.addModule(ModuleRegistry.create(SimpleDamageModule.id));
//        projectile.addTraits(new CollisionTrait(Constants.COLLISION_TYPE, "Blaze", "Fireball", "AbstractArrow", "FireProjectile"));
//        projectile.addCollisionModule((ICollisionModule) ModuleRegistry.create(FireCollisionModule.id));
//
//        // Slow down over time
//        projectile.addTraits(skillData.getTrait(Constants.SPEED_FACTOR, SpeedTrait.class));
//        projectile.addModule(ModuleRegistry.create(ChangeSpeedModule.id));
//
//        // Particle FX
//        projectile.shoot(entity.position().add(0, entity.getEyeHeight(), 0).add(entity.getLookAngle().scale(-1)), entity.getLookAngle(), speed, 0.5);
//        projectile.setFX(skillData.getTrait(Constants.FX, StringTrait.class).getInfo());
//        projectile.setFXOneShot(false);
//        projectile.lookDirection(entity.getLookAngle().toVector3f());
//        projectile.init();
//
//        ServerLevel serverLevel = (ServerLevel) bender.getEntity().level();
//        // Ensure entity is added on the main server thread after current tick
//        serverLevel.getServer().execute(() -> serverLevel.addFreshEntity(projectile));
//    }
}