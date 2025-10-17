package com.amuzil.omegasource.bending.element.fire.forms;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.*;
import com.amuzil.omegasource.bending.skill.FireForm;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.utils.Constants;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import static com.amuzil.omegasource.bending.form.BendingForms.PUSH;


public class FirePushForm extends FireForm {

    public FirePushForm() {
        super(Avatar.MOD_ID, "fire_push");
        addTrait(new ColourTrait(0, 0, 0, Constants.FIRE_COLOUR));
        addTrait(new TimedTrait(Constants.LIFETIME, 20));
        addTrait(new SpeedTrait(Constants.SPEED, 1.5d));
        addTrait(new StringTrait(Constants.FX, "fires_bloom_perma5"));

        startPaths = SkillPathBuilder.getInstance().simple(new ActiveForm(PUSH, true)).build();
    }

    @Override
    public boolean shouldStart(Bender bender, FormPath formPath) {
        return formPath.simple().hashCode() == startPaths().simple().hashCode();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);

        LivingEntity benderEntity = bender.getEntity();
        Level level = benderEntity.level();

        int lifetime = skillData.getTrait(Constants.LIFETIME, TimedTrait.class).getTime();
        double speed = skillData.getTrait(Constants.SPEED, SpeedTrait.class).getSpeed();

        HitResult hitResult = ProjectileUtil.getHitResultOnViewVector(benderEntity, entity -> true, 15);
        if (hitResult instanceof EntityHitResult result) {
            Entity target = result.getEntity();
            Vec3 direction = target.position().subtract(benderEntity.position()).normalize();
            Vec3 pushVelocity = direction.scale(speed);

            target.setDeltaMovement(target.getDeltaMovement().add(pushVelocity));
            target.hurtMarked = true;
            target.hasImpulse = true;

            Avatar.LOGGER.info("Fire Push applied to {} with velocity {}", target.getName().getString(), pushVelocity);
        }

        skillData.setSkillState(SkillState.IDLE);

    }
}
