package com.amuzil.omegasource.bending.skill;

import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.bending.BendingSkill;
import com.amuzil.omegasource.bending.element.Elements;


public class FireSkill extends BendingSkill {

    public FireSkill(String modID, String name) {
        super(modID, name, Elements.FIRE);
    }

    @Override
    public SkillCategory getCategory() {
        return Elements.FIRE;
    }
}
