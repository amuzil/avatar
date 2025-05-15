package com.amuzil.omegasource.bending.element.fire;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.ColourTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.KnockbackTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.SpeedTrait;
import com.amuzil.omegasource.bending.BendingEffect;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.bending.form.BendingForm;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

import static com.amuzil.omegasource.bending.form.BendingForms.STEP;

public class FlameStepSkill extends BendingEffect {

    public FlameStepSkill() {
        super(Avatar.MOD_ID, "flame_step", Elements.FIRE);
        addTrait(new KnockbackTrait(1.5f, Constants.KNOCKBACK));
        addTrait(new SpeedTrait(3f, Constants.DASH_SPEED));
        addTrait(new SizeTrait(1.0f, Constants.SIZE));
        addTrait(new ColourTrait(0, 0, 0, Constants.FIRE_COLOUR));

        this.startPaths = SkillPathBuilder.getInstance()
                .complex(new ActiveForm(STEP, true))
                .build();

        this.runPaths = SkillPathBuilder.getInstance()
                .complex(new ActiveForm(STEP, true))
                .build();
    }

    @Override
    public SkillCategory getCategory() {
        return Elements.FIRE;
    }

    @Override
    public void start(LivingEntity entity) {
        super.start(entity);

        Bender bender = (Bender) Bender.getBender(entity);
        if (bender != null) {
            BendingForm.Type.Motion motion = bender.getFormPath().complex().get(0).direction();
            SkillData data = bender.getSkillData(this);

            if (!entity.level().isClientSide()) {
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
                }
                dashVec = dashVec.add(0, 0.3D, 0); // Add a little hop for better dash
//                System.out.println("Dash Vec: " + dashVec);
                entity.setDeltaMovement(dashVec);
                entity.hurtMarked = true;
                entity.hasImpulse = true;
            }

//        ((Player) entity).jumpFromGround();
//        System.out.println("Delta: " + entity.getDeltaMovement());
//        entity.setDeltaMovement(entity.getDeltaMovement().multiply(5,1,5).scale(dashSpeed).add(0,0.3D,0));
//        entity.hurtMarked = true; // Mark the entity for velocity sync
//        System.out.println("New Delta: " + entity.getDeltaMovement());

            data.setState(SkillState.RUN);
            resetCooldown(data);
        }
    }

    @Override
    public void run(LivingEntity entity) {
        super.run(entity);
        if (!entity.level().isClientSide()) {
            entity.fallDistance = 0.0F;
        }
    }
}
