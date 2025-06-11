package com.amuzil.omegasource.bending.element.air;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.traits.entitytraits.PointsTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.*;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.bending.skill.AirSkill;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.entity.AvatarProjectile;
import com.amuzil.omegasource.entity.modules.IRenderModule;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import com.amuzil.omegasource.utils.Constants;
import com.amuzil.omegasource.utils.maths.Point;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static com.amuzil.omegasource.bending.form.BendingForms.STRIKE;


public class AirGustSkill extends AirSkill {

    public AirGustSkill() {
        super(Avatar.MOD_ID, "air_gust");
        addTrait(new DamageTrait(Constants.DAMAGE, 1.5f));
        addTrait(new SizeTrait(Constants.SIZE, 0.125F));
        addTrait(new SizeTrait(Constants.MAX_SIZE, 1.25f));
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 0.6f));
        addTrait(new SpeedTrait(Constants.SPEED, 0.875d));
        addTrait(new TimedTrait(Constants.LIFETIME, 15)); // Ticks not seconds...
        addTrait(new SpeedTrait(Constants.SPEED_FACTOR, 0.85d));
        addTrait(new StringTrait(Constants.FX, "airs_perma8"));

        startPaths = SkillPathBuilder.getInstance().simple(new ActiveForm(STRIKE, true)).build();
    }

    @Override
    public boolean shouldStart(Bender bender, FormPath formPath) {
        return formPath.simple().hashCode() == getStartPaths().simple().hashCode();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);

        LivingEntity entity = bender.getEntity();
        Level level = bender.getEntity().level();
        SkillData data = bender.getSkillData(this);

        int lifetime = data.getTrait(Constants.LIFETIME, TimedTrait.class).getTime();
        double speed = data.getTrait(Constants.SPEED, SpeedTrait.class).getSpeed();
        double size = data.getTrait(Constants.SIZE, SizeTrait.class).getSize();

        AvatarProjectile projectile = new AvatarProjectile(level);
        projectile.setElement(Elements.AIR);
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

        projectile.addTraits(data.getTrait(Constants.KNOCKBACK, KnockbackTrait.class));
        projectile.addTraits(new DirectionTrait("knockback_direction", new Vec3(0, 0.45, 0)));
        projectile.addModule(ModuleRegistry.create("SimpleKnockback"));

        // Damage module
        projectile.addTraits(data.getTrait(Constants.DAMAGE, DamageTrait.class));
        projectile.addModule(ModuleRegistry.create("SimpleDamage"));

        // Slow down over time
        projectile.addTraits(data.getTrait(Constants.SPEED_FACTOR, SpeedTrait.class));
        projectile.addModule(ModuleRegistry.create("ChangeSpeed"));

        // Particle FX module
        projectile.addTraits(data.getTrait(Constants.FX, StringTrait.class));

        projectile.shoot(entity.position().add(0, entity.getEyeHeight(), 0), entity.getLookAngle(), speed, 0);
        projectile.init();

        bender.formPath.clear();
        data.setSkillState(SkillState.IDLE);

        if (!bender.getEntity().level().isClientSide) {
            bender.getEntity().level().addFreshEntity(projectile);
        }
    }
}
