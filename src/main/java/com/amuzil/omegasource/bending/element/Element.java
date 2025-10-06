package com.amuzil.omegasource.bending.element;

import com.amuzil.omegasource.api.magus.skill.SkillCategory;


public abstract class Element extends SkillCategory {
    private final String nickName;
    private final Type type;

    public Element(String modId, String name, Type type) {
        super(modId, name);
        this.nickName = name.replace("bending", "");
        this.type = type;
        Elements.ALL_FOUR.put(nickName, this);
    }

    public String nickName() { return nickName; }

    public Type type() {
        return type;
    }

    @Override
    public String toString() { return String.format("Element[ %s ]", name); }

    public enum Type {
        AIR,
        WATER,
        EARTH,
        FIRE;
    }
}
