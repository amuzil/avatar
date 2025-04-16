package com.amuzil.omegasource.bending.element.fire;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.capability.entity.Magi;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.radix.RadixTree;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.skill.utils.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.utils.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.utils.traits.skilltraits.*;
import com.amuzil.omegasource.bending.BendingEffect;
import com.amuzil.omegasource.bending.BendingSkill;
import com.amuzil.omegasource.bending.element.Elements;

import com.amuzil.omegasource.entity.ElementProjectile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import static com.amuzil.omegasource.bending.BendingForms.*;

public class FireStrikeEffect extends BendingEffect {

    public FireStrikeEffect() {
        super(Avatar.MOD_ID, "fire_strike_effect", Elements.FIRE);
        addTrait(new DamageTrait(3.0f, "damage"));
        addTrait(new SizeTrait(1.0f, "size"));
        addTrait(new KnockbackTrait(1.5f, "knockback"));
        addTrait(new SpeedTrait(3.0f, "speed"));
        addTrait(new ColourTrait(0, 0, 0, "fire_colour"));

    }

    @Override
    public FormPath getStartPaths() {
        return SkillPathBuilder.getInstance()
                .simple(new ActiveForm(STRIKE, true))
                .build();
    }

    @Override
    public boolean shouldStart(LivingEntity entity, FormPath formPath) {
        boolean shouldStart = false;
        Magi magi = Magi.get(entity);
        if (magi != null) {
            SkillData data = magi.getSkillData(this);
            if (data.getState().equals(SkillState.START)) {
                shouldStart = true;
            }
            else if (data.getState().equals(SkillState.IDLE)) {
               shouldStart = checkCooldown(data);
            }
        }

        return super.shouldStart(entity, formPath) && shouldStart;
    }

    @Override
    public SkillCategory getCategory() {
        return Elements.FIRE;
    }

    @Override
    public void start(LivingEntity entity) {
        super.start(entity);
//        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED));
//        entity.level().addFreshEntity(new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level()));



        Magi magi = Magi.get(entity);
        if (magi != null) {
            SkillData data = magi.getSkillData(this);
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
