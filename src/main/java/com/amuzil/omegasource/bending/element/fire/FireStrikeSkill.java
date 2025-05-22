package com.amuzil.omegasource.bending.element.fire;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.traits.entitytraits.PointsTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.*;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.bending.skill.FireSkill;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.entity.AvatarProjectile;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import com.amuzil.omegasource.utils.Constants;
import com.amuzil.omegasource.utils.maths.Point;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static com.amuzil.omegasource.bending.form.BendingForms.STRIKE;


public class FireStrikeSkill extends FireSkill {

    public FireStrikeSkill() {
        super(Avatar.MOD_ID, "fire_strike");
        addTrait(new DamageTrait(2.5f, Constants.DAMAGE));
        addTrait(new SizeTrait(0.125F, Constants.SIZE));
        addTrait(new SizeTrait(1.25f, Constants.MAX_SIZE));
        addTrait(new KnockbackTrait(0.5f, Constants.KNOCKBACK));
        addTrait(new ColourTrait(0, 0, 0, Constants.FIRE_COLOUR));
        addTrait(new SpeedTrait(0.875d, Constants.SPEED));
        addTrait(new TimedTrait(15, Constants.LIFETIME)); // Ticks not seconds...
        addTrait(new TimedTrait(40, Constants.FIRE_TIME));
        addTrait(new SpeedTrait(0.85d, Constants.SPEED_FACTOR));

        startPaths = SkillPathBuilder.getInstance().complex(new ActiveForm(STRIKE, true)).build();
    }

    @Override
    public boolean shouldStart(LivingEntity entity, FormPath formPath) {
        return super.shouldStart(entity, formPath);
    }

    @Override
    public void start(LivingEntity entity) {
        super.start(entity);

        Bender bender = (Bender) Bender.getBender(entity);
        Level level = entity.level();
        SkillData data = bender.getSkillData(this);

        int lifetime = data.getTrait(Constants.LIFETIME, TimedTrait.class).getTime();
        double speed = data.getTrait(Constants.SPEED, SpeedTrait.class).getSpeed();
        double size = data.getTrait(Constants.SIZE, SizeTrait.class).getSize();

        AvatarProjectile projectile = new AvatarProjectile(level);
        projectile.setElement(Elements.FIRE);
        projectile.setOwner(entity);
        projectile.setMaxLifetime(lifetime);
        projectile.setWidth((float) size);
        projectile.setHeight((float) size);
        projectile.setNoGravity(true);
        projectile.setDamageable(false);
        projectile.setHittable(true);

        projectile.addTraits(data.getTrait(Constants.MAX_SIZE, SizeTrait.class));

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

        projectile.addModule(ModuleRegistry.create("Grow"));

        // TODO: make more advanced knockback calculator that uses the entity's current size as well as speed (mass * velocity!!!!)
        projectile.addTraits(data.getTrait(Constants.KNOCKBACK, KnockbackTrait.class));
        projectile.addTraits(new DirectionTrait("knockback_direction", new Vec3(0, 0.45, 0)));
        projectile.addModule(ModuleRegistry.create("SimpleKnockback"));

        // Set Fire module
        projectile.addTraits(data.getTrait(Constants.FIRE_TIME, TimedTrait.class));
        projectile.addModule(ModuleRegistry.create("FireTime"));

        // Damage module
        projectile.addTraits(data.getTrait(Constants.DAMAGE, DamageTrait.class));
        projectile.addModule(ModuleRegistry.create("SimpleDamage"));

        // Slow down over time
        projectile.addTraits(data.getTrait(Constants.SPEED_FACTOR, SpeedTrait.class));
        projectile.addModule(ModuleRegistry.create("ChangeSpeed"));

        projectile.shoot(entity.position().add(0, entity.getEyeHeight(), 0), entity.getLookAngle(), speed, 0);
        projectile.init();
        if (bender != null) {
            bender.formPath.clear();
            data.setState(SkillState.STOP);

            resetCooldown(data);
        }

        if (!entity.level().isClientSide) {
            entity.level().addFreshEntity(projectile);
        }
    }
}
