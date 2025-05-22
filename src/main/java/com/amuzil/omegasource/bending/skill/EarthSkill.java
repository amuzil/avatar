package com.amuzil.omegasource.bending.skill;

import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.bending.BendingSkill;
import com.amuzil.omegasource.bending.element.Elements;


public class EarthSkill extends BendingSkill {

    public EarthSkill(String modID, String name) {
        super(modID, name, Elements.EARTH);
    }

    @Override
    public SkillCategory getCategory() {
        return Elements.EARTH;
    }


}
