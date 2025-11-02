package com.amuzil.av3.bending.skill;

import com.amuzil.av3.api.magus.skill.SkillCategory;
import com.amuzil.av3.bending.BendingSkill;
import com.amuzil.av3.bending.element.Elements;


public abstract class EarthSkill extends BendingSkill {

    public EarthSkill(String modID, String name) {
        super(modID, name);
    }

    @Override
    public SkillCategory getCategory() {
        return Elements.EARTH;
    }


}
