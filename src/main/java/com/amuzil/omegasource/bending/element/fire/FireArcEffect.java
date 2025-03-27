package com.amuzil.omegasource.bending.element.fire;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.skill.FormPath;
import com.amuzil.omegasource.api.magus.skill.SkillActive;
import com.amuzil.omegasource.api.magus.skill.utils.data.SkillPathBuilder;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.bending.form.ActiveForm;
import com.amuzil.omegasource.bending.form.Forms;
import com.amuzil.omegasource.registry.Registries;

public class FireArcEffect extends SkillActive {

    public FireArcEffect() {
        super(Avatar.MOD_ID, "fire_arc_effect", Elements.FIRE);
    }

    @Override
    public FormPath getStartPaths() {
        return SkillPathBuilder.getInstance()
                .complexForm(new ActiveForm(Forms.STRIKE, true))
                .simpleForm(new ActiveForm(Forms.STRIKE, true))
                .build();
    }


}
