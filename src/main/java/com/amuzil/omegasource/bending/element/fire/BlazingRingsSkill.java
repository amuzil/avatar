package com.amuzil.omegasource.bending.element.fire;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.traits.entitytraits.PointsTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.*;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.bending.form.BendingForms;
import com.amuzil.omegasource.bending.skill.FireSkill;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.entity.api.ICollisionModule;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import com.amuzil.omegasource.entity.modules.collision.FireCollisionModule;
import com.amuzil.omegasource.entity.modules.collision.FireModule;
import com.amuzil.omegasource.entity.modules.collision.SimpleKnockbackModule;
import com.amuzil.omegasource.entity.modules.entity.GrowModule;
import com.amuzil.omegasource.entity.projectile.AvatarOrbitProjectile;
import com.amuzil.omegasource.utils.Constants;
import com.amuzil.omegasource.utils.maths.Point;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


public class BlazingRingsSkill extends FireSkill {

    public BlazingRingsSkill() {
        super(Avatar.MOD_ID, "blazing_rings");
        addTrait(new DamageTrait(Constants.DAMAGE, 2.5f));
        addTrait(new SizeTrait(Constants.SIZE, 0.125F));
        addTrait(new SizeTrait(Constants.MAX_SIZE, 1.25f));
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 0.4f));
        addTrait(new ColourTrait(0, 0, 0, Constants.FIRE_COLOUR));
        addTrait(new SpeedTrait(Constants.SPEED, 0.4d));
        addTrait(new TimedTrait(Constants.LIFETIME, 40));
        addTrait(new TimedTrait(Constants.FIRE_TIME, 40));
        addTrait(new StringTrait(Constants.FX, "fires_bloom_perma6"));
        addTrait(new AngleTrait(Constants.ANGLE, 0));
        addTrait(new RangeTrait(Constants.RANGE, 2.0d));

        startPaths = SkillPathBuilder.getInstance()
                .simple(new ActiveForm(BendingForms.ARC, true))
                .simple(new ActiveForm(BendingForms.ROTATE, true))
                .build();
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
        double size = data.getTrait(Constants.SIZE, SizeTrait.class).getSize();

        AvatarOrbitProjectile projectile = new AvatarOrbitProjectile(level);
        projectile.setElement(Elements.FIRE);
        projectile.setFX(data.getTrait(Constants.FX, StringTrait.class).getInfo());
        projectile.setOwner(entity);
        projectile.setMaxLifetime(lifetime);
        projectile.setWidth((float) size);
        projectile.setHeight((float) size);
        projectile.setNoGravity(true);
        projectile.setDamageable(false);
        projectile.setHittable(true);

        projectile.addTraits(data.getTrait(Constants.MAX_SIZE, SizeTrait.class));
        projectile.addTraits(data.getTrait(Constants.ANGLE, AngleTrait.class));
        projectile.addTraits(data.getTrait(Constants.SPEED, SpeedTrait.class));
        projectile.addTraits(data.getTrait(Constants.RANGE, RangeTrait.class));

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

        projectile.addTraits(data.getTrait(Constants.KNOCKBACK, KnockbackTrait.class));
        projectile.addTraits(new DirectionTrait("knockback_direction", new Vec3(0, 0.45, 0)));
        projectile.addModule(ModuleRegistry.create(SimpleKnockbackModule.id));

        // Set Fire module
        projectile.addTraits(data.getTrait(Constants.FIRE_TIME, TimedTrait.class));
        projectile.addModule(ModuleRegistry.create(FireModule.id));

        // Damage module
        projectile.addTraits(data.getTrait(Constants.DAMAGE, DamageTrait.class));
        projectile.addTraits(data.getTrait(Constants.SIZE, SizeTrait.class));
//        projectile.addModule(ModuleRegistry.create(SimpleDamageModule.id));
        projectile.addTraits(new CollisionTrait(Constants.COLLISION_TYPE, "Blaze", "Fireball", "AbstractArrow", "FireProjectile"));
        projectile.addCollisionModule((ICollisionModule) ModuleRegistry.create(FireCollisionModule.id));

        // Particle FX
        projectile.init();

        bender.formPath.clear();
        data.setSkillState(SkillState.IDLE);

        if (!bender.getEntity().level().isClientSide) {
            bender.getEntity().level().addFreshEntity(projectile);
        }
    }
}
