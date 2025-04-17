package com.amuzil.omegasource.bending.element.fire;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.capability.entity.Magi;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.radix.RadixTree;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.skill.utils.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.utils.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.utils.traits.skilltraits.ColourTrait;
import com.amuzil.omegasource.api.magus.skill.utils.traits.skilltraits.KnockbackTrait;
import com.amuzil.omegasource.api.magus.skill.utils.traits.skilltraits.SizeTrait;
import com.amuzil.omegasource.api.magus.skill.utils.traits.skilltraits.SpeedTrait;
import com.amuzil.omegasource.bending.BendingEffect;
import com.amuzil.omegasource.bending.element.Elements;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

import static com.amuzil.omegasource.bending.BendingForms.STEP;

public class FlameStepSkill extends BendingEffect {

    public FlameStepSkill() {
        super(Avatar.MOD_ID, "flame_step", Elements.FIRE);
        addTrait(new KnockbackTrait(1.5f, "knockback"));
        addTrait(new SpeedTrait(1.5f, "dash_speed"));
        addTrait(new SizeTrait(1.0f, "size"));
        addTrait(new ColourTrait(0, 0, 0, "fire_colour"));

        this.startPaths = SkillPathBuilder.getInstance()
                .complex(new ActiveForm(STEP, true))
                .build();

    }

    @Override
    public boolean shouldStart(LivingEntity entity, FormPath formPath) {
        return super.shouldStart(entity, formPath);
    }

    @Override
    public SkillCategory getCategory() {
        return Elements.FIRE;
    }

    @Override
    public void start(LivingEntity entity) {
        super.start(entity);

        Magi magi = Magi.get(entity);
        if (magi != null) {
            SkillData data = magi.getSkillData(this);

//        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED));
//        entity.level().addFreshEntity(new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level()));
//        if (!entity.level().isClientSide) {
//            ElementProjectile proj;
//            proj = ElementProjectile.createElementEntity(STRIKE, Elements.FIRE, (ServerPlayer) entity, (ServerLevel) entity.level());
//            proj.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, 1, 1);
//            entity.level().addFreshEntity(proj);
//            RadixTree.getLogger().info("Attempting projectile spawn.");
//        }

            float dashSpeed = (float) Objects.requireNonNull(data.getTrait("dash_speed", SpeedTrait.class)).getSpeed();
            Vec3 dashVec = Vec3.ZERO;
            if (entity.level().isClientSide) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.options.keyUp.isDown()) {
                    dashVec = entity.getLookAngle().multiply(1, 0, 1).normalize().scale(dashSpeed); // W
                } else if (mc.options.keyDown.isDown()) {
                    dashVec = entity.getLookAngle().multiply(1, 0, 1).normalize().scale(-dashSpeed); // S
                } else if (mc.options.keyLeft.isDown()) {
                    dashVec = entity.getLookAngle().cross(new Vec3(0, 1, 0)).normalize().scale(-dashSpeed); // A
                } else if (mc.options.keyRight.isDown()) {
                    dashVec = entity.getLookAngle().cross(new Vec3(0, 1, 0)).normalize().scale(dashSpeed); // D
                }
            }
            entity.setDeltaMovement(dashVec.x, entity.getDeltaMovement().y + 0.3D, dashVec.z);

//        ((Player) entity).jumpFromGround();
//        System.out.println("Delta: " + entity.getDeltaMovement());
//        entity.setDeltaMovement(entity.getDeltaMovement().multiply(5,1,5).scale(dashSpeed).add(0,0.3D,0));
//        entity.hurtMarked = true; // Mark the entity for velocity sync
//        System.out.println("New Delta: " + entity.getDeltaMovement());


            magi.formPath.clear();
            data.setState(SkillState.IDLE);

            resetCooldown(data);
        }

//        for (SkillTrait trait : getTraits()) {
//            if (trait instanceof StringTrait) {
//                if (trait.getName().equals("skill_state"))
//                    ((StringTrait) trait).setInfo("stop");
//            }
//        }

    }

}
