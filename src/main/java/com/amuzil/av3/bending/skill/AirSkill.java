package com.amuzil.av3.bending.skill;

import com.amuzil.magus.skill.SkillCategory;
import com.amuzil.av3.bending.BendingSkill;
import com.amuzil.av3.bending.element.Elements;


public abstract class AirSkill extends BendingSkill {

    public AirSkill(String modID, String name) {
        super(modID, name);
    }

    @Override
    public SkillCategory getCategory() {
        return Elements.AIR;
    }
}
