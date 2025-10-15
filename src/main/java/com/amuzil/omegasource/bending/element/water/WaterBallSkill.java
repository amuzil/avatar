package com.amuzil.omegasource.bending.element.water;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.*;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.bending.skill.WaterSkill;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.entity.projectile.AvatarCurveProjectile;
import com.amuzil.omegasource.entity.api.ICollisionModule;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import com.amuzil.omegasource.entity.modules.collision.SimpleKnockbackModule;
import com.amuzil.omegasource.entity.modules.collision.WaterCollisionModule;
import com.amuzil.omegasource.utils.Constants;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static com.amuzil.omegasource.bending.form.BendingForms.STRIKE;


public class WaterBallSkill extends WaterSkill {

    public WaterBallSkill() {
        super(Avatar.MOD_ID, "water_ball");
        addTrait(new DamageTrait(Constants.DAMAGE, 2.0f));
        addTrait(new SizeTrait(Constants.SIZE, 0.3F));
        addTrait(new SizeTrait(Constants.MAX_SIZE, 1.25f));
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 0.2f));
        addTrait(new SpeedTrait(Constants.SPEED, 0.475d));
        addTrait(new TimedTrait(Constants.LIFETIME, 35)); // Ticks not seconds...
        addTrait(new SpeedTrait(Constants.SPEED_FACTOR, 0.85d));
        addTrait(new StringTrait(Constants.FX, "water1"));

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
        SkillData data = bender.getSkillData(this);

        int lifetime = data.getTrait(Constants.LIFETIME, TimedTrait.class).getTime();
        double speed = data.getTrait(Constants.SPEED, SpeedTrait.class).getSpeed();
        double size = data.getTrait(Constants.SIZE, SizeTrait.class).getSize();

        AvatarCurveProjectile projectile = new AvatarCurveProjectile(level);
        projectile.setElement(Elements.WATER);
        projectile.setFX(data.getTrait(Constants.FX, StringTrait.class).getInfo());
        projectile.setOwner(entity);
        projectile.setMaxLifetime(lifetime);
        projectile.setWidth((float) size);
        projectile.setHeight((float) size);
        projectile.setNoGravity(true);
        projectile.setDamageable(false);
        projectile.setHittable(true);

        projectile.addTraits(data.getTrait(Constants.KNOCKBACK, KnockbackTrait.class));
        projectile.addTraits(new DirectionTrait("knockback_direction", new Vec3(0, 0.45, 0)));
        projectile.addModule(ModuleRegistry.create(SimpleKnockbackModule.id));

        // Damage module
        projectile.addTraits(data.getTrait(Constants.DAMAGE, DamageTrait.class));
        projectile.addTraits(data.getTrait(Constants.SIZE, SizeTrait.class));
//        projectile.addModule(ModuleRegistry.create(SimpleDamageModule.id));
        projectile.addTraits(new CollisionTrait(Constants.COLLISION_TYPE, "Blaze", "Fireball", "AbstractArrow", "FireProjectile"));
        projectile.addCollisionModule((ICollisionModule) ModuleRegistry.create(WaterCollisionModule.id));

        // Slow down over time
        projectile.addTraits(data.getTrait(Constants.SPEED_FACTOR, SpeedTrait.class));
//        projectile.addModule(ModuleRegistry.create(ChangeSpeedModule.id));

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
