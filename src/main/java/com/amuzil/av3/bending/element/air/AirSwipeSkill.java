package com.amuzil.av3.bending.element.air;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.skill.AirSkill;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.entity.api.ICollisionModule;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.collision.AirCollisionModule;
import com.amuzil.av3.entity.api.modules.collision.SimpleKnockbackModule;
import com.amuzil.av3.entity.api.modules.entity.GrowModule;
import com.amuzil.av3.entity.projectile.AvatarProjectile;
import com.amuzil.av3.utils.Constants;
import com.amuzil.av3.utils.maths.Point;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.traits.entitytraits.PointsTrait;
import com.amuzil.magus.skill.traits.skilltraits.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

import static com.amuzil.av3.bending.form.BendingForms.COMPRESS;
import static com.amuzil.av3.utils.bending.ProjectileFactory.createDirectProjectile;


public class AirSwipeSkill extends AirSkill {

    public AirSwipeSkill() {
        super(Avatar.MOD_ID, "air_swipe");
        addTrait(new DamageTrait(Constants.DAMAGE, 2.5f));
        addTrait(new SizeTrait(Constants.SIZE, 0.125F));
        addTrait(new SizeTrait(Constants.MAX_SIZE, 1.25f));
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 0.6f));
        addTrait(new SpeedTrait(Constants.SPEED, 1.5d));
        addTrait(new TimedTrait(Constants.LIFETIME, 30));
        addTrait(new StringTrait(Constants.FX, "airs_perma12"));

        
        startPaths = SkillPathBuilder.getInstance()
//                .simple(new ActiveForm(SHAPE, true))
                .add(COMPRESS)
                .build();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);

        LivingEntity entity = bender.getEntity();
        Level level = bender.getEntity().level();

        int lifetime = skillData.getTrait(Constants.LIFETIME, TimedTrait.class).getTime();
        double speed = skillData.getTrait(Constants.SPEED, SpeedTrait.class).getSpeed();
        double size = skillData.getTrait(Constants.SIZE, SizeTrait.class).getSize();
        String fxName = this.skillData.getTrait(Constants.FX, StringTrait.class).getInfo();

        AvatarProjectile projectile = createDirectProjectile(level, element(), entity, lifetime, (float) size, fxName);

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

        projectile.addTraits(skillData.getTrait(Constants.KNOCKBACK, KnockbackTrait.class));
        projectile.addTraits(new DirectionTrait(Constants.KNOCKBACK_DIRECTION, new Vec3(0, 0.45, 0)));
        projectile.addModule(ModuleRegistry.create(SimpleKnockbackModule.id));

        // Damage module
        projectile.addTraits(skillData.getTrait(Constants.DAMAGE, DamageTrait.class));
        projectile.addTraits(skillData.getTrait(Constants.SIZE, SizeTrait.class));
//        projectile.addModule(ModuleRegistry.create(SimpleDamageModule.id));
        projectile.addTraits(new CollisionTrait(Constants.COLLISION_TYPE, "Blaze", "Fireball", "AbstractArrow", "FireProjectile"));
        projectile.addCollisionModule((ICollisionModule) ModuleRegistry.create(AirCollisionModule.id));

        projectile.shoot(entity.position().add(0, entity.getEyeHeight(), 0), entity.getLookAngle(), speed, 0);
        projectile.init();

        bender.formPath.clear();
        skillData.setSkillState(SkillState.IDLE);

        bender.getEntity().level().addFreshEntity(projectile);
    }
}
