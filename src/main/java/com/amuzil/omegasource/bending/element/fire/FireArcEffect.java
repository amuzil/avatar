package com.amuzil.omegasource.bending.element.fire;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.skill.FormPath;
import com.amuzil.omegasource.api.magus.skill.SkillActive;
import com.amuzil.omegasource.api.magus.skill.utils.capability.entity.Magi;
import com.amuzil.omegasource.api.magus.skill.utils.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.utils.traits.SkillTrait;
import com.amuzil.omegasource.api.magus.skill.utils.traits.skilltraits.StringTrait;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.bending.form.ActiveForm;
import com.amuzil.omegasource.bending.form.Forms;
import com.amuzil.omegasource.entity.ElementProjectile;
import com.amuzil.omegasource.registry.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;

import static com.amuzil.omegasource.bending.form.Forms.*;
import static com.amuzil.omegasource.bending.form.Forms.BLOCK;

public class FireArcEffect extends SkillActive {

    public FireArcEffect() {
        super(Avatar.MOD_ID, "fire_arc_effect", Elements.FIRE);
        addTrait(new StringTrait("skill_state", "start"));
    }

    @Override
    public FormPath getStartPaths() {
        return SkillPathBuilder.getInstance()
                .addForm(new ActiveForm(LOWER, false))
                .build();
    }

    @Override
    public boolean shouldStart(LivingEntity entity, FormPath formPath) {
        boolean shouldStart = false;
        for (SkillTrait trait : getTraits()) {
            if (trait instanceof StringTrait) {
                if (trait.getName().equals("skill_state"))
                    shouldStart = ((StringTrait) trait).getInfo().equals("start");
            }
        }
        return super.shouldStart(entity, formPath);// && shouldStart;
    }

    @Override
    public void start(LivingEntity entity) {
        super.start(entity);
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED));

//        for (SkillTrait trait : getTraits()) {
//            if (trait instanceof StringTrait) {
//                if (trait.getName().equals("skill_state"))
//                    ((StringTrait) trait).setInfo("stop");
//            }
//        }

    }

}
