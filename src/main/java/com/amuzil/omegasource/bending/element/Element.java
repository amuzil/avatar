package com.amuzil.omegasource.bending.element;

import com.amuzil.omegasource.api.magus.skill.SkillCategory;


public class Element extends SkillCategory {
    private final Art art;

    public Element(Art art) {
        super(art.toString());
        this.art = art;
        Elements.LIST.add(this);
    }

    public Art type() {
        return art;
    }

    @Override
    public String toString() { return String.format("Element[ %s ]", name); }

    public enum Art {
        AIR, WATER, EARTH, FIRE;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}
