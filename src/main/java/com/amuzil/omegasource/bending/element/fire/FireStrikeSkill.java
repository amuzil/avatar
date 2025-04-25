package com.amuzil.omegasource.bending.element.fire;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.traits.entitytraits.PointsTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.*;
import com.amuzil.omegasource.bending.BendingSkill;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.entity.AvatarProjectile;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import com.amuzil.omegasource.utils.maths.Point;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static com.amuzil.omegasource.bending.BendingForms.STRIKE;


public class FireStrikeSkill extends BendingSkill {

    public FireStrikeSkill() {
        super(Avatar.MOD_ID, "fire_strike", Elements.FIRE);
        addTrait(new DamageTrait(2.5f, "damage"));
        addTrait(new SizeTrait(0.125F, "size"));
        addTrait(new SizeTrait(1.25f, "max_size"));
        addTrait(new KnockbackTrait(0.5f, "knockback"));
        addTrait(new ColourTrait(0, 0, 0, "fire_colour"));
        addTrait(new SpeedTrait(0.675f, "speed"));
        addTrait(new TimedTrait(15, "lifetime"));
        // Ticks not seconds...
        addTrait(new TimedTrait(40, "firetime"));
        addTrait(new SpeedTrait(0.95f, "slow_factor"));

        startPaths = SkillPathBuilder.getInstance().complex(new ActiveForm(STRIKE, false)).build();

    }


    @Override
    public boolean shouldStart(LivingEntity entity, FormPath formPath) {
        return super.shouldStart(entity, formPath);
    }

    @Override
    public void start(LivingEntity entity) {
        super.start(entity);

        Bender bender = (Bender) Bender.getBender(entity);
        // Resets data so we can test
//        bender.resetData();
        Level level = entity.level();
        SkillData data = bender.getSkillData(this);

        int lifetime = data.getTrait("lifetime", TimedTrait.class).getTime();
        double speed = data.getTrait("speed", SpeedTrait.class).getSpeed();
        double size = data.getTrait("size", SizeTrait.class).getSize();

        AvatarProjectile projectile = new AvatarProjectile(level);
        projectile.setElement(Elements.FIRE);
        projectile.setOwner(entity);
        projectile.setMaxLifetime(lifetime);
        projectile.setWidth((float) size);
        projectile.setHeight((float) size);
        projectile.setNoGravity(true);
        projectile.setDamageable(false);
        projectile.setCollidable(true);

        projectile.addTraits(data.getTrait("max_size", SizeTrait.class));

        // Copied from the fire easing constant
        projectile.addTraits(new PointsTrait("height_curve", new Point(0.00, 0.00),  // t=0: zero width
                new Point(0.20, 0.25),  // rise slowly
                new Point(0.40, 3),  // flare to 150%
                new Point(0.70, 0.40),  // rapid taper
                new Point(1.00, 0.00)   // die out completely
        ));

        // Used for bezier curving
        projectile.addTraits(new PointsTrait("width_curve", new Point(0.00, 0.00),  // t=0: zero width
                new Point(0.20, 0.25),  // rise slowly
                new Point(0.40, 2),  // flare to 150%
                new Point(0.70, 0.40),  // rapid taper
                new Point(1.00, 0.00)   // die out completely
        ));

        projectile.addModule(ModuleRegistry.create("Grow"));

        // TODO: make more advanced knockback calculator that uses the entity's current size as well as speed (mass * velocity!!!!)
        projectile.addTraits(data.getTrait("knockback", KnockbackTrait.class));
        projectile.addTraits(new DirectionTrait("knockback_direction", new Vec3(0, 0.45, 0)));
        projectile.addModule(ModuleRegistry.create("SimpleKnockback"));

        // Set Fire module
        projectile.addTraits(data.getTrait("firetime", TimedTrait.class));
        projectile.addModule(ModuleRegistry.create("FireTime"));

        // Damage module
        projectile.addTraits(data.getTrait("damage", DamageTrait.class));
        projectile.addModule(ModuleRegistry.create("SimpleDamage"));

        if (!entity.level().isClientSide) {
//            proj = ElementProjectile.createElementEntity(STRIKE, Elements.FIRE, (ServerPlayer) entity, (ServerLevel) entity.level());
            entity.level().addFreshEntity(projectile);
        }

        projectile.shoot(entity.position().add(0, entity.getEyeHeight(), 0), entity.getLookAngle(), speed, 0);
        projectile.init();
        if (bender != null) {
            bender.formPath.clear();
            data.setState(SkillState.STOP);

            resetCooldown(data);
        }
    }


    @Override
    public SkillCategory getCategory() {
        return Elements.FIRE;
    }

}
