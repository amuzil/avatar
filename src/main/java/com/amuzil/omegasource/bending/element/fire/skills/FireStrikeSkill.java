package com.amuzil.omegasource.bending.element.fire.skills;

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
import com.amuzil.omegasource.entity.api.ICollisionModule;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import com.amuzil.omegasource.entity.modules.collision.FireCollisionModule;
import com.amuzil.omegasource.entity.modules.collision.FireModule;
import com.amuzil.omegasource.entity.modules.collision.SimpleKnockbackModule;
import com.amuzil.omegasource.entity.modules.entity.GrowModule;
import com.amuzil.omegasource.entity.projectile.AvatarDirectProjectile;
import com.amuzil.omegasource.utils.Constants;
import com.amuzil.omegasource.utils.maths.Point;
import com.amuzil.omegasource.utils.sound.AvatarEntitySound;
import com.amuzil.omegasource.utils.sound.AvatarSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import static com.amuzil.omegasource.bending.form.BendingForms.STRIKE;


public class FireStrikeSkill extends FireSkill {

    public FireStrikeSkill() {
        super(Avatar.MOD_ID, "fire_strike");
        addTrait(new DamageTrait(Constants.DAMAGE, 2.5f));
        addTrait(new SizeTrait(Constants.SIZE, 0.125F));
        addTrait(new SizeTrait(Constants.MAX_SIZE, 1.25f));
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 0.4f));
        addTrait(new ColourTrait(0, 0, 0, Constants.FIRE_COLOUR));
        addTrait(new SpeedTrait(Constants.SPEED, 0.875d));
        addTrait(new TimedTrait(Constants.LIFETIME, 15));
        addTrait(new TimedTrait(Constants.FIRE_TIME, 40));
        addTrait(new SpeedTrait(Constants.SPEED_FACTOR, 0.85d));
        addTrait(new StringTrait(Constants.FX, "fires_bloom_perma5"));

        startPaths = SkillPathBuilder.getInstance().simple(new ActiveForm(STRIKE, true)).build();
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
        else {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft.getInstance().getSoundManager()
                        .play(new AvatarEntitySound(projectile, AvatarSounds.FIRE_STRIKE.get(), lifetime));
            });
        }
    }
}
