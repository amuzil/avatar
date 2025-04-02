package com.amuzil.omegasource.bending.element;

import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.registry.Registries;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;

import java.util.ArrayList;
import java.util.List;


public class Element extends SkillCategory {
    private final String name;
    private final Art art;


    public Element(Art art) {
        this.name = art.name().toLowerCase();
        this.art = art;
        Registries.registerElement(this);
    }

    public String name() {
        return name;
    }

    public Art type() {
        return art;
    }

    public enum Art {
        AIR, WATER, EARTH, FIRE
    }

    // Used for client-side rendering and categorisation.
    public List<Skill> getSkills() {
        List<Skill> skillCopy = new ArrayList<>(Registries.getSkills());
        skills.forEach(skill -> skill.getCategory().equals(this));
        return skillCopy;
    }
}
