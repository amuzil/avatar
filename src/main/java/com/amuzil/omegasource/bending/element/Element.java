package com.amuzil.omegasource.bending.element;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;


public abstract class Element extends SkillCategory {

    public Element(String modid, String name) {
        super(Avatar.MOD_ID, name);
    }


    @Override
    public String toString() { return String.format("Element[ %s ]", name); }
}
