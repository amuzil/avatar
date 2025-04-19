package com.amuzil.omegasource.api.magus.skill;

import com.amuzil.omegasource.api.magus.registry.Registries;


public class SkillCategory {
    protected final String name;

    public SkillCategory(String name) {
        this.name = name;
        Registries.registerSkillCategory(this);
    }

    public String name() { return name; }
}
