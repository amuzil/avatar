package com.amuzil.omegasource.bending.element.fire.forms;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.*;
import com.amuzil.omegasource.bending.skill.FireForm;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.utils.Constants;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import static com.amuzil.omegasource.bending.form.BendingForms.PULL;


public class FirePullForm extends FireForm {

    public FirePullForm() {
        super(Avatar.MOD_ID, "fire_pull");
        addTrait(new ColourTrait(0, 0, 0, Constants.FIRE_COLOUR));
        addTrait(new TimedTrait(Constants.LIFETIME, 20));
        addTrait(new SpeedTrait(Constants.SPEED, 0.875d));
        addTrait(new StringTrait(Constants.FX, "fires_bloom_perma5"));

        startPaths = SkillPathBuilder.getInstance().simple(new ActiveForm(PULL, true)).build();
    }

    @Override
    public boolean shouldStart(Bender bender, FormPath formPath) {
        return formPath.simple().hashCode() == startPaths().simple().hashCode();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);

        LivingEntity entity = bender.getEntity();
        Level level = bender.getEntity().level();
        SkillData data = bender.getSkillData(this);
        System.out.println("Starting Fire Pull Form");

        int lifetime = data.getTrait(Constants.LIFETIME, TimedTrait.class).getTime();
        double speed = data.getTrait(Constants.SPEED, SpeedTrait.class).getSpeed();

        data.setSkillState(SkillState.IDLE);

    }
}
