package com.amuzil.av3.bending.element.fire;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.form.BendingForm;
import com.amuzil.av3.bending.skill.FireSkill;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.entity.projectile.AvatarProjectile;
import com.amuzil.av3.utils.Constants;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.traits.skilltraits.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

import static com.amuzil.av3.bending.form.BendingForms.STEP;
import static com.amuzil.av3.utils.bending.ProjectileFactory.createBoundProjectile;


public class FlameStepSkill extends FireSkill {

    public FlameStepSkill() {
        super(Avatar.MOD_ID, "flame_step");
        addTrait(new SpeedTrait(Constants.DASH_SPEED, 1.5f));
        addTrait(new SizeTrait(Constants.SIZE, 0.5f));
        addTrait(new ColourTrait(0, 0, 0, Constants.FIRE_COLOUR));
        addTrait(new TimedTrait(Constants.MAX_RUNTIME, 60));
        addTrait(new TimedTrait(Constants.RUNTIME, 0));
        addTrait(new StringTrait(Constants.FX, "fires_bloom"));

        this.startPaths = SkillPathBuilder.getInstance()
                .add(STEP)
                .build();
    }

    @Override
    public void start(Bender bender) {
        super.startRun();
        LivingEntity entity = bender.getEntity();
        Level level = bender.getEntity().level();

        BendingForm.Type.Motion motion = bender.getStepDirection();
        if (motion == BendingForm.Type.Motion.NONE)
            motion = BendingForm.Type.Motion.FORWARD;

        int lifetime = skillData.getTrait(Constants.MAX_RUNTIME, TimedTrait.class).getTime();
        double size = skillData.getTrait(Constants.SIZE, SizeTrait.class).getSize();
        TimedTrait time = skillData.getTrait(Constants.RUNTIME, TimedTrait.class);
        String fxName = skillData.getTrait(Constants.FX, StringTrait.class).getInfo();
        time.setTime(0); // Reset fall damage nullification timer

        AvatarProjectile bound = createBoundProjectile(level, element(), entity, lifetime, (float) size, fxName);
        bound.init();
        entity.level().addFreshEntity(bound);

        float dashSpeed = (float) Objects.requireNonNull(
                skillData.getTrait(Constants.DASH_SPEED, SpeedTrait.class)).getSpeed();
        Vec3 dashVec = Vec3.ZERO;
        switch (motion) {
            case FORWARD -> dashVec = entity.getLookAngle().multiply(1, 0, 1).normalize().scale(dashSpeed); // W
            case BACKWARD -> dashVec = entity.getLookAngle().multiply(1, 0, 1).normalize().scale(-dashSpeed); // S
            case LEFTWARD -> dashVec = entity.getLookAngle().cross(new Vec3(0, 1, 0)).normalize().scale(-dashSpeed); // A
            case RIGHTWARD -> dashVec = entity.getLookAngle().cross(new Vec3(0, 1, 0)).normalize().scale(dashSpeed); // D
            case UPWARD -> dashVec = bender.getDeltaMovement().add(0, dashSpeed / 2, 0).multiply(4, 1, 4); // SPACE
            case DOWNWARD -> dashVec = bender.getDeltaMovement().add(0, -(dashSpeed / 3), 0);
        }
        if (motion != BendingForm.Type.Motion.DOWNWARD)
            dashVec = dashVec.add(0, 0.3D, 0); // Add a little hop for better dash
        entity.setDeltaMovement(dashVec);
        entity.hurtMarked = true;
        entity.hasImpulse = true;
    }

    @Override
    public void run(Bender bender) {
        super.run(bender);
        bender.getEntity().fallDistance = 0.0F;
        incrementTimedTrait(skillData, Constants.RUNTIME, skillData.getTrait(Constants.MAX_RUNTIME, TimedTrait.class).getTime());
    }
}
