package com.amuzil.omegasource.bending.element.earth;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.*;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.bending.form.BendingForm;
import com.amuzil.omegasource.bending.skill.EarthSkill;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.entity.AvatarBoundEntity;
import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.utils.Constants;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

import static com.amuzil.omegasource.bending.form.BendingForms.STEP;


public class EarthStepSkill extends EarthSkill {

    public EarthStepSkill() {
        super(Avatar.MOD_ID, "earth_step");
        addTrait(new SpeedTrait(Constants.DASH_SPEED, 1.5f));
        addTrait(new SizeTrait(Constants.SIZE, 1.0f));
        addTrait(new ColourTrait(139, 69, 19, "earth_colour"));
        addTrait(new TimedTrait(Constants.MAX_RUNTIME, 60));
        addTrait(new TimedTrait(Constants.RUNTIME, 0));
        addTrait(new StringTrait(Constants.FX, "earth_bloom_perma5"));
        this.startPaths = SkillPathBuilder.getInstance()
                .simple(new ActiveForm(STEP, true))
                .complex(new ActiveForm(STEP, true))
                .build();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);
        LivingEntity entity = bender.getEntity();
        BendingForm.Type.Motion motion;
        if (!bender.getFormPath().complex().isEmpty())
            motion = bender.getFormPath().complex().get(0).direction();
        else
            motion = BendingForm.Type.Motion.FORWARD;

        SkillData data = bender.getSkillData(this);
        int lifetime = data.getTrait(Constants.MAX_RUNTIME, TimedTrait.class).getTime();
        TimedTrait time = data.getTrait(Constants.RUNTIME, TimedTrait.class);
        time.setTime(0);

        AvatarEntity bound = new AvatarBoundEntity(entity.level());
        bound.setElement(Elements.EARTH);
        bound.setFX(data.getTrait(Constants.FX, StringTrait.class).getInfo());
        bound.setOwner(entity);
        bound.setMaxLifetime(lifetime / 3);
        bound.setNoGravity(true);
        bound.setPos(entity.position());
        bound.init();

        if (!entity.level().isClientSide()) {
            entity.level().addFreshEntity(bound);

            float dashSpeed = (float) Objects.requireNonNull(
                    data.getTrait(Constants.DASH_SPEED, SpeedTrait.class)).getSpeed();
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

        data.setSkillState(SkillState.RUN);
    }

    @Override
    public void run(Bender bender) {
        super.run(bender);
        if (!bender.getEntity().level().isClientSide()) {
            SkillData data = bender.getSkillData(this);
            bender.getEntity().fallDistance = 0.0F;
            incrementTimedTrait(data, Constants.RUNTIME,
                    data.getTrait(Constants.MAX_RUNTIME, TimedTrait.class).getTime());
        }
    }
}
