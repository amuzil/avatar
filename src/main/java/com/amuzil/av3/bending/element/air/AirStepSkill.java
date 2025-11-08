package com.amuzil.av3.bending.element.air;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.element.Elements;
import com.amuzil.av3.bending.form.BendingForm;
import com.amuzil.av3.bending.skill.AirSkill;
import com.amuzil.av3.capability.Bender;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.projectile.AvatarBoundProjectile;
import com.amuzil.av3.utils.Constants;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.traits.skilltraits.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

import static com.amuzil.av3.bending.form.BendingForms.STEP;


public class AirStepSkill extends AirSkill {

    public AirStepSkill() {
        super(Avatar.MOD_ID, "air_step");
        addTrait(new SpeedTrait(Constants.DASH_SPEED, 1.7f));
        addTrait(new SizeTrait(Constants.SIZE, 1.0f));
        addTrait(new ColourTrait(0, 0, 0, "air_colour"));
        addTrait(new TimedTrait(Constants.MAX_RUNTIME, 60));
        addTrait(new TimedTrait(Constants.RUNTIME, 0));
        addTrait(new StringTrait(Constants.FX, "airs_perma8"));
        this.startPaths = SkillPathBuilder.getInstance()
                .add(STEP)
                .build();
    }

    @Override
    public void start(Bender bender) {
        super.startRun();
        LivingEntity entity = bender.getEntity();

        BendingForm.Type.Motion motion = bender.getStepDirection();
        if (motion == BendingForm.Type.Motion.NONE)
            motion = BendingForm.Type.Motion.FORWARD;

        int lifetime = skillData.getTrait(Constants.MAX_RUNTIME, TimedTrait.class).getTime();
        TimedTrait time = skillData.getTrait(Constants.RUNTIME, TimedTrait.class);
        time.setTime(0);

        AvatarEntity bound = new AvatarBoundProjectile(entity.level());
        bound.setElement(Elements.AIR);
        bound.setFX(skillData.getTrait(Constants.FX, StringTrait.class).getInfo());
        bound.setOwner(entity);
        bound.setMaxLifetime(lifetime / 3);
        bound.setNoGravity(true);
        bound.setPos(entity.position());
        bound.init();

        if (!entity.level().isClientSide()) {
            entity.level().addFreshEntity(bound);

            float dashSpeed = (float) Objects.requireNonNull(
                    skillData.getTrait(Constants.DASH_SPEED, SpeedTrait.class)).getSpeed();
            Vec3 dashVec = Vec3.ZERO;
            switch (motion) {
                case FORWARD -> dashVec = entity.getLookAngle().multiply(1, 0, 1).normalize().scale(dashSpeed);
                case BACKWARD -> dashVec = entity.getLookAngle().multiply(1, 0, 1).normalize().scale(-dashSpeed);
                case LEFTWARD -> dashVec = entity.getLookAngle().cross(new Vec3(0, 1, 0)).normalize().scale(-dashSpeed);
                case RIGHTWARD -> dashVec = entity.getLookAngle().cross(new Vec3(0, 1, 0)).normalize().scale(dashSpeed);
                case UPWARD -> dashVec = bender.getDeltaMovement().add(0, dashSpeed / 2, 0).multiply(4, 1, 4);
                case DOWNWARD -> dashVec = bender.getDeltaMovement().add(0, -(dashSpeed / 3), 0);
            }
            if (motion != BendingForm.Type.Motion.DOWNWARD)
                dashVec = dashVec.add(0, 0.3D, 0);

            entity.setDeltaMovement(dashVec);
            entity.hurtMarked = true;
            entity.hasImpulse = true;
        }
    }

    @Override
    public void run(Bender bender) {
        super.run(bender);
        if (!bender.getEntity().level().isClientSide()) {
            bender.getEntity().fallDistance = 0.0F;
            incrementTimedTrait(skillData, Constants.RUNTIME,
                    skillData.getTrait(Constants.MAX_RUNTIME, TimedTrait.class).getTime());
        }
    }
}
