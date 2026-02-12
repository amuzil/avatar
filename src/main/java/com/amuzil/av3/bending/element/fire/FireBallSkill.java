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
import com.amuzil.av3.entity.api.modules.entity.GrowModule;
import com.amuzil.av3.entity.api.modules.force.GravityModule;
import com.amuzil.av3.entity.projectile.AvatarDirectProjectile;
import com.amuzil.av3.utils.Constants;
import com.amuzil.av3.utils.maths.Point;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.traits.entitytraits.PointsTrait;
import com.amuzil.magus.skill.traits.skilltraits.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import static com.amuzil.av3.bending.form.BendingForms.STRIKE;

public class FireBallSkill extends FireSkill {

    public FireBallSkill() {
        super(Avatar.MOD_ID, "fireball");
        addTrait(new DamageTrait(Constants.DAMAGE, 2.5f));
        addTrait(new SizeTrait(Constants.SIZE, 0.5F));
        addTrait(new SizeTrait(Constants.MAX_SIZE, 0.875f));
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 0.1f));
        addTrait(new ColourTrait(0, 0, 0, Constants.FIRE_COLOUR));
        addTrait(new SpeedTrait(Constants.SPEED, 1.25d));
        addTrait(new TimedTrait(Constants.LIFETIME, 60));
        addTrait(new TimedTrait(Constants.FIRE_TIME, 40));
        addTrait(new SpeedTrait(Constants.SPEED_FACTOR, 0.95d));
        addTrait(new StringTrait(Constants.FX, "fireball"));

        startPaths = SkillPathBuilder.getInstance().add(BendingForms.COMPRESS).build();
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
        projectile.vfxScale(new Vector3f(2, 2, 2));
//        projectile.addModule(ModuleRegistry.create(SimpleDamageModule.id));
        projectile.addTraits(new CollisionTrait(Constants.COLLISION_TYPE, "Blaze", "Fireball", "AbstractArrow", "FireProjectile"));
        projectile.addCollisionModule((ICollisionModule) ModuleRegistry.create(FireCollisionModule.id));

        // Slow down over time
        projectile.addTraits(skillData.getTrait(Constants.SPEED_FACTOR, SpeedTrait.class));
        projectile.addModule(ModuleRegistry.create(GravityModule.id));
//        projectile.addModule(ModuleRegistry.create(ChangeSpeedModule.id));

        // Particle FX
        projectile.lookDirection(entity.getLookAngle().toVector3f());
        projectile.setFXOneShot(false);
        projectile.shoot(entity.position().add(0, entity.getEyeHeight(), 0), entity.getLookAngle(), speed, 0);
        projectile.init();

        bender.formPath.clear();
        skillData.setSkillState(SkillState.IDLE);

        bender.getEntity().level().addFreshEntity(projectile);
//        else {
//            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
//                Minecraft.getInstance().getSoundManager()
//                        .play(new AvatarEntitySound(projectile, AvatarSounds.FIRE_STRIKE.get(), lifetime));
//            });
//        }
    }
}
