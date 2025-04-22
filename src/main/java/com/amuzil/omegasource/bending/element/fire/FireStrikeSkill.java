package com.amuzil.omegasource.bending.element.fire;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.capability.entity.Magi;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.*;
import com.amuzil.omegasource.bending.BendingSkill;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.entity.ElementProjectile;
import com.amuzil.omegasource.entity.projectile.FireProjectile;
import net.minecraft.world.entity.LivingEntity;

import static com.amuzil.omegasource.bending.BendingForms.STRIKE;

public class FireStrikeSkill extends BendingSkill {

    public FireStrikeSkill() {
        super(Avatar.MOD_ID, "fire_strike", Elements.FIRE);
        addTrait(new DamageTrait(2.5f, "damage"));
        addTrait(new SizeTrait(1.5F, "size"));
        addTrait(new KnockbackTrait(2f, "knockback"));
        addTrait(new ColourTrait(0, 0, 0, "fire_colour"));
        addTrait(new SpeedTrait(1.5f, "speed"));
        addTrait(new TimedTrait(15, "lifetime"));

        startPaths = SkillPathBuilder.getInstance().complex(new ActiveForm(STRIKE, true)).build();

    }


    @Override
    public boolean shouldStart(LivingEntity entity, FormPath formPath) {
        return super.shouldStart(entity, formPath);
    }

    @Override
    public void start(LivingEntity entity) {
        super.start(entity);

        Bender bender = (Bender) Bender.getBender(entity);
        SkillData data = bender.getSkillData(this);

        int lifetime = data.getTrait("lifetime", TimedTrait.class).getTime();
        double speed = data.getTrait("speed", SpeedTrait.class).getSpeed();

        Avatar.LOGGER.debug("Fire Strike Speed: " + speed);
        ElementProjectile proj = new FireProjectile(entity, entity.level());
        proj.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, (float) speed, 1);
        proj.setTimeToKill(lifetime);
        if (!entity.level().isClientSide) {
//            proj = ElementProjectile.createElementEntity(STRIKE, Elements.FIRE, (ServerPlayer) entity, (ServerLevel) entity.level());



            entity.level().addFreshEntity(proj);
        }
        if (bender != null) {
            bender.formPath.clear();
            data.setState(SkillState.STOP);

            resetCooldown(data);
        }
    }



    @Override
    public SkillCategory getCategory() {
        return Elements.FIRE;
    }
}
