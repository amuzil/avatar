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
import com.amuzil.omegasource.entity.api.ICollisionModule;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import com.amuzil.omegasource.entity.modules.collision.AirCollisionModule;
import com.amuzil.omegasource.entity.modules.collision.SimpleKnockbackModule;
import com.amuzil.omegasource.entity.modules.entity.GrowModule;
import com.amuzil.omegasource.entity.modules.force.ChangeSpeedModule;
import com.amuzil.omegasource.entity.projectile.AvatarDirectProjectile;
import com.amuzil.omegasource.utils.Constants;
import com.amuzil.omegasource.utils.maths.Point;
import com.amuzil.omegasource.utils.sound.AvatarEntitySound;
import com.amuzil.omegasource.utils.sound.AvatarSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import static com.amuzil.omegasource.bending.form.BendingForms.*;


public class AirPullSkill extends AirSkill {

    public AirPullSkill() {
        super(Avatar.MOD_ID, "air_pull");
        addTrait(new DamageTrait(Constants.DAMAGE, 0.5f));
        addTrait(new SizeTrait(Constants.SIZE, 0.125F));
        addTrait(new SizeTrait(Constants.MAX_SIZE, 1.25f));
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 0.6f));
        addTrait(new SpeedTrait(Constants.SPEED, 0.875d));
        addTrait(new TimedTrait(Constants.LIFETIME, 15)); // Ticks not seconds...
        addTrait(new SpeedTrait(Constants.SPEED_FACTOR, 1.0d));
        addTrait(new StringTrait(Constants.FX, "airs_perma10"));

        startPaths = SkillPathBuilder.getInstance()
                .simple(new ActiveForm(ARC, true))
                .simple(new ActiveForm(PULL, true))
                .build();
    }

    @Override
    public boolean shouldStart(Bender bender, FormPath formPath) {
        return formPath.simple().hashCode() == startPaths().simple().hashCode();
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

        AvatarDirectProjectile projectile = new AvatarDirectProjectile(level);
        projectile.setElement(Elements.AIR);
        projectile.setFX(data.getTrait(Constants.FX, StringTrait.class).getInfo());
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

        projectile.addModule(ModuleRegistry.create(GrowModule.id));

        projectile.addTraits(data.getTrait(Constants.KNOCKBACK, KnockbackTrait.class));
        projectile.addTraits(new DirectionTrait("knockback_direction", entity.getLookAngle().reverse()));
        projectile.addModule(ModuleRegistry.create(SimpleKnockbackModule.id));

        // Damage module
        projectile.addTraits(data.getTrait(Constants.DAMAGE, DamageTrait.class));
        projectile.addTraits(data.getTrait(Constants.SIZE, SizeTrait.class));
//        projectile.addModule(ModuleRegistry.create(SimpleDamageModule.id));
        projectile.addTraits(new CollisionTrait(Constants.COLLISION_TYPE, "Blaze", "Fireball", "AbstractArrow", "FireProjectile"));
        projectile.addCollisionModule((ICollisionModule) ModuleRegistry.create(AirCollisionModule.id));

        // Slow down over time
        projectile.addTraits(data.getTrait(Constants.SPEED_FACTOR, SpeedTrait.class));
        projectile.addModule(ModuleRegistry.create(ChangeSpeedModule.id));

        // Particle FX module
        projectile.addTraits(data.getTrait(Constants.FX, StringTrait.class));

        projectile.shoot(entity.position().add(0, entity.getEyeHeight(), 0).add(entity.getLookAngle().scale(8)), entity.getLookAngle().reverse(), speed, 0);
        projectile.init();

        bender.formPath.clear();
        data.setSkillState(SkillState.IDLE);

        if (!bender.getEntity().level().isClientSide) {
            bender.getEntity().level().addFreshEntity(projectile);
        } else {
            Minecraft.getInstance().getSoundManager()
                    .play(new AvatarEntitySound(projectile, AvatarSounds.AIR_GUST.get()));
        }
    }
}
