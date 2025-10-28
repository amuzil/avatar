package com.amuzil.omegasource.bending.skill;

import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.bending.BendingSkill;
import com.amuzil.omegasource.bending.element.Elements;


public abstract class FireSkill extends BendingSkill {

    public FireSkill(String modID, String name) {
        super(modID, name);
    }

    @Override
    public SkillCategory getCategory() {
        return Elements.FIRE;
    }
}
