package com.amuzil.omegasource.bending.skill;

import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.bending.BendingSkill;
import com.amuzil.omegasource.bending.element.Elements;


public class WaterSkill extends BendingSkill {

    public WaterSkill(String modID, String name) {
        super(modID, name, Elements.WATER);
    }

    @Override
    public SkillCategory getCategory() {
        return Elements.WATER;
    }
}
