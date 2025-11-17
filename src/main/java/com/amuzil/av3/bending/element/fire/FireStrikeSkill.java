package com.amuzil.av3.bending.element.fire;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.skill.FireSkill;
import com.amuzil.av3.capability.Bender;
import com.amuzil.av3.entity.api.ICollisionModule;
import com.amuzil.av3.entity.modules.ModuleRegistry;
import com.amuzil.av3.entity.modules.collision.FireCollisionModule;
import com.amuzil.av3.entity.modules.collision.FireModule;
import com.amuzil.av3.entity.modules.collision.SimpleKnockbackModule;
import com.amuzil.av3.entity.modules.entity.GrowModule;
import com.amuzil.av3.entity.projectile.AvatarDirectProjectile;
import com.amuzil.av3.utils.Constants;
import com.amuzil.av3.utils.maths.Point;
import com.amuzil.carryon.physics.bullet.collision.body.shape.MinecraftShape;
import com.amuzil.carryon.physics.bullet.collision.space.MinecraftSpace;
import com.amuzil.magus.physics.core.ForceCloud;
import com.amuzil.magus.physics.core.ForcePoint;
import com.amuzil.magus.physics.core.ForceSystem;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.traits.entitytraits.PointsTrait;
import com.amuzil.magus.skill.traits.skilltraits.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static com.amuzil.av3.bending.form.BendingForms.STRIKE;


public class FireStrikeSkill extends FireSkill {

    public FireStrikeSkill() {
        super(Avatar.MOD_ID, "fire_strike");
        addTrait(new DamageTrait(Constants.DAMAGE, 2.5f));
        addTrait(new SizeTrait(Constants.SIZE, 0.125F));
        addTrait(new SizeTrait(Constants.MAX_SIZE, 1.25f));
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 0.1f));
        addTrait(new ColourTrait(0, 0, 0, Constants.FIRE_COLOUR));
        addTrait(new SpeedTrait(Constants.SPEED, 0.875d));
        addTrait(new TimedTrait(Constants.LIFETIME, 15));
        addTrait(new TimedTrait(Constants.FIRE_TIME, 40));
        addTrait(new SpeedTrait(Constants.SPEED_FACTOR, 0.85d));
        addTrait(new StringTrait(Constants.FX, "fires_bloom_perma5"));

        startPaths = SkillPathBuilder.getInstance().add(STRIKE).build();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);

        LivingEntity entity = bender.getEntity();
        Level level = bender.getEntity().level();

        int lifetime = skillData.getTrait(Constants.LIFETIME, TimedTrait.class).getTime();
        double speed = skillData.getTrait(Constants.SPEED, SpeedTrait.class).getSpeed();
        double size = skillData.getTrait(Constants.SIZE, SizeTrait.class).getSize();

        AvatarDirectProjectile projectile = new AvatarDirectProjectile(level);
        projectile.setElement(element());
        projectile.setFX(skillData.getTrait(Constants.FX, StringTrait.class).getInfo());
        projectile.setOwner(entity);
        projectile.setMaxLifetime(lifetime);
        projectile.setWidth((float) size);
        projectile.setHeight((float) size);
        projectile.setNoGravity(true);
        projectile.setDamageable(false);
        projectile.setHittable(true);

        projectile.addTraits(skillData.getTrait(Constants.MAX_SIZE, SizeTrait.class));

        // Copied from the fire easing constant
        projectile.addTraits(new PointsTrait("height_curve", new Point(0.00, 0.5),  // t=0: zero width
                new Point(0.20, 0.75),  // rise slowly
                new Point(0.40, 2.5),  // flare to 150%
                new Point(0.70, 0.40),  // rapid taper
                new Point(1.00, 0.00)   // die out completely
        ));

        // Used for bezier curving
        projectile.addTraits(new PointsTrait("width_curve", new Point(0.00, 0.5),  // t=0: zero width
                new Point(0.20, 0.75),  // rise slowly
                new Point(0.40, 1.75),  // flare to 150%
                new Point(0.70, 0.40),  // rapid taper
                new Point(1.00, 0.00)   // die out completely
        ));

        projectile.addModule(ModuleRegistry.create(GrowModule.id));

        // TODO: make more advanced knockback calculator that uses the entity's current size as well as speed (mass * velocity!!!!)
        projectile.addTraits(skillData.getTrait(Constants.KNOCKBACK, KnockbackTrait.class));
        projectile.addTraits(new DirectionTrait(Constants.KNOCKBACK_DIRECTION, new Vec3(0, 0.25, 0)));
        projectile.addModule(ModuleRegistry.create(SimpleKnockbackModule.id));

        // Set Fire module
        projectile.addTraits(skillData.getTrait(Constants.FIRE_TIME, TimedTrait.class));
        projectile.addModule(ModuleRegistry.create(FireModule.id));

        // Damage module
        projectile.addTraits(skillData.getTrait(Constants.DAMAGE, DamageTrait.class));
        projectile.addTraits(skillData.getTrait(Constants.SIZE, SizeTrait.class));
//        projectile.addModule(ModuleRegistry.create(SimpleDamageModule.id));
        projectile.addTraits(new CollisionTrait(Constants.COLLISION_TYPE, "Blaze", "Fireball", "AbstractArrow", "FireProjectile"));
        projectile.addCollisionModule((ICollisionModule) ModuleRegistry.create(FireCollisionModule.id));

        // Slow down over time
        projectile.addTraits(skillData.getTrait(Constants.SPEED_FACTOR, SpeedTrait.class));
//        projectile.addModule(ModuleRegistry.create(ChangeSpeedModule.id));

        // Particle FX
        projectile.shoot(entity.position().add(0, entity.getEyeHeight(), 0), entity.getLookAngle(), speed, 0);
        projectile.init();

        bender.formPath.clear();
        skillData.setSkillState(SkillState.IDLE);


        if (!bender.getEntity().level().isClientSide) {
            bender.getEntity().level().addFreshEntity(projectile);
        }


        // Test physics

//        MinecraftSpace space = MinecraftSpace.get(level);
//        // 4-5 clouds doing flamethrower shit
//        // 20 clouds doing flamethrower shit
//        // 20 * 200 * 20 = 800,000
//        // 20 * 50 * 20  = 200,000
//
//        if (space != null) {
//            ForceSystem fs = space.forceSystem();
//
//            // type is whatever you use for element (e.g. FIRE = 1, WATER = 2, etc.)
//            int type = 1; // example
//            int maxPoints = 2000;
//
//            ForceCloud cloud = fs.createCloud(type, maxPoints);
//            cloud.setLifetimeSeconds(3.0f);
//
//            // create some points
//            int count = 400;
//            Vec3 origin = projectile.position();
//            Vec3 direction = entity.getLookAngle();
//            for (int i = 0; i < count; i++) {
//                // scatter a bit around origin
//                double rx = (level.random.nextDouble() - 0.5) * 0.5;
//                double ry = (level.random.nextDouble() - 0.5) * 0.5;
//                double rz = (level.random.nextDouble() - 0.5) * 0.5;
//
//                Vec3 pos = origin.add(rx, ry, rz);
//
//                // initial velocity roughly in 'direction'
//                Vec3 vel = direction.normalize().scale(0.3)
//                        .add((level.random.nextDouble() - 0.5) * 0.1,
//                                (level.random.nextDouble() - 0.5) * 0.1,
//                                (level.random.nextDouble() - 0.5) * 0.1);
//
//                Vec3 force = Vec3.ZERO; // start with no force, just velocity
//
//                ForcePoint p = new ForcePoint(type, pos, vel, force);
//                p.mass(1.0);    // if you have mass setters
//                p.damping(0.1); // mild drag
////                if (level instanceof ServerLevel server && entity instanceof ServerPlayer)
////                   server.sendParticles((ServerPlayer) entity, ParticleTypes.SMOKE, false, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0.1f);
//
//                cloud.addPoints(p);
//            }
//        }
//        else {
//            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
//                Minecraft.getInstance().getSoundManager()
//                        .play(new AvatarEntitySound(projectile, AvatarSounds.FIRE_STRIKE.get(), lifetime));
//            });
//        }
    }
}
