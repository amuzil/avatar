package com.amuzil.omegasource.api.magus.skill;

import com.amuzil.omegasource.registry.Registries;

import java.util.LinkedList;


public class SkillCategory {

    // List of all skills available within the category
    public LinkedList<Skill> skills = new LinkedList<>();

    public String name() { return "SkillCategory[ Blank ]"; }

    public void registerSkills() {
        Registries.registerSkills(skills);
    }
}
