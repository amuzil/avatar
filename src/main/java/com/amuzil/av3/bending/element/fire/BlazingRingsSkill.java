package com.amuzil.av3.bending.element.fire;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.form.BendingForms;
import com.amuzil.av3.bending.skill.FireSkill;
import com.amuzil.av3.capability.Bender;
import com.amuzil.av3.entity.api.ICollisionModule;
import com.amuzil.av3.entity.modules.ModuleRegistry;
import com.amuzil.av3.entity.modules.collision.FireCollisionModule;
import com.amuzil.av3.entity.modules.collision.FireModule;
import com.amuzil.av3.entity.modules.collision.SimpleKnockbackModule;
import com.amuzil.av3.entity.modules.entity.GrowModule;
import com.amuzil.av3.entity.projectile.AvatarOrbitProjectile;
import com.amuzil.av3.utils.Constants;
import com.amuzil.av3.utils.maths.Point;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.traits.entitytraits.PointsTrait;
import com.amuzil.magus.skill.traits.skilltraits.*;
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
//                .simple(new ActiveForm(BendingForms.ARC, true))
                .add(BendingForms.ROTATE)
                .build();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);

        LivingEntity entity = bender.getEntity();
        Level level = bender.getEntity().level();

        int lifetime = skillData.getTrait(Constants.LIFETIME, TimedTrait.class).getTime();
        double size = skillData.getTrait(Constants.SIZE, SizeTrait.class).getSize();

        AvatarOrbitProjectile projectile = new AvatarOrbitProjectile(level);
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
        projectile.addTraits(skillData.getTrait(Constants.ANGLE, AngleTrait.class));
        projectile.addTraits(skillData.getTrait(Constants.SPEED, SpeedTrait.class));
        projectile.addTraits(skillData.getTrait(Constants.RANGE, RangeTrait.class));

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

        projectile.addTraits(skillData.getTrait(Constants.KNOCKBACK, KnockbackTrait.class));
        projectile.addTraits(new DirectionTrait(Constants.KNOCKBACK_DIRECTION, new Vec3(0, 0.45, 0)));
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

        // Particle FX
        projectile.init();

        bender.formPath.clear();
        skillData.setSkillState(SkillState.IDLE);

        if (!bender.getEntity().level().isClientSide) {
            bender.getEntity().level().addFreshEntity(projectile);
        }
    }
}
