package com.amuzil.omegasource.bending.element.fire;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.*;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.bending.form.BendingForm;
import com.amuzil.omegasource.bending.skill.FireSkill;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.AvatarProjectile;
import com.amuzil.omegasource.entity.modules.IForceModule;
import com.amuzil.omegasource.entity.modules.IRenderModule;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import com.amuzil.omegasource.entity.modules.render.ParticleModule;
import com.amuzil.omegasource.utils.Constants;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

import static com.amuzil.omegasource.bending.form.BendingForms.STEP;


public class FlameStepSkill extends FireSkill {

    public FlameStepSkill() {
        super(Avatar.MOD_ID, "flame_step");
        addTrait(new SpeedTrait(Constants.DASH_SPEED, 1.25f));
        addTrait(new SizeTrait(Constants.SIZE, 1.0f));
        addTrait(new ColourTrait(0, 0, 0, Constants.FIRE_COLOUR));
        addTrait(new TimedTrait(Constants.MAX_RUNTIME, 60));
        addTrait(new TimedTrait(Constants.RUNTIME, 0));
        addTrait(new StringTrait(Constants.FX, "fire_bloom_perma5"));

        this.startPaths = SkillPathBuilder.getInstance()
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
            motion = BendingForm.Type.Motion.FORWARD; // Default to forward if no motion is specified
        SkillData data = bender.getSkillData(this);
        int lifetime = data.getTrait(Constants.MAX_RUNTIME, TimedTrait.class).getTime();
        AvatarEntity projectile = new AvatarProjectile(entity.level());
        projectile.setElement(Elements.FIRE);
        projectile.setFX(data.getTrait(Constants.FX, StringTrait.class).getInfo());
        projectile.setOwner(entity);
        projectile.setMaxLifetime(lifetime);
        projectile.setNoGravity(true);
        projectile.setPos(entity.position());
        projectile.init();

        if (!bender.getEntity().level().isClientSide()) {
            bender.getEntity().level().addFreshEntity(projectile);
            float dashSpeed = (float) Objects.requireNonNull(data.getTrait(Constants.DASH_SPEED, SpeedTrait.class)).getSpeed();
            Vec3 dashVec = Vec3.ZERO;
            switch (motion) {
                case FORWARD ->
                        dashVec = entity.getLookAngle().multiply(1, 0, 1).normalize().scale(dashSpeed); // W
                case BACKWARD ->
                        dashVec = entity.getLookAngle().multiply(1, 0, 1).normalize().scale(-dashSpeed); // S
                case LEFTWARD ->
                        dashVec = entity.getLookAngle().cross(new Vec3(0, 1, 0)).normalize().scale(-dashSpeed); // A
                case RIGHTWARD ->
                        dashVec = entity.getLookAngle().cross(new Vec3(0, 1, 0)).normalize().scale(dashSpeed); // D
                case UPWARD ->
                    dashVec = bender.getDeltaMovement().add(0, dashSpeed / 3, 0).multiply(4, 1, 4); // SPACE
                case DOWNWARD ->
                    dashVec = bender.getDeltaMovement().add(0, dashSpeed / 3, 0).multiply(-2, -1, -2);
            }
            dashVec = dashVec.add(0, 0.3D, 0); // Add a little hop for better dash
//                System.out.println("Dash Vec: " + dashVec);
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
            incrementTimedTrait(data, Constants.RUNTIME, data.getTrait(Constants.MAX_RUNTIME, TimedTrait.class).getTime());
        }
    }
}
