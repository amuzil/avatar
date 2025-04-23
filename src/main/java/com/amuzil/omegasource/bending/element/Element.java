package com.amuzil.omegasource.bending.element;

import com.amuzil.omegasource.api.magus.skill.SkillCategory;


public abstract class Element extends SkillCategory {
    private final String nickName;

    public Element(String modId, String name) {
        super(modId, name);
        nickName = name.replace("bending", "");
        Elements.ALL_FOUR.put(nickName, this);
    }

    public String nickName() { return nickName; }

    @Override
    public String toString() { return String.format("Element[ %s ]", name); }
}
