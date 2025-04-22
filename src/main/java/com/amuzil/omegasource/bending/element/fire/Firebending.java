package com.amuzil.omegasource.bending.element.fire;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.bending.element.Element;

public class Firebending {//extends Element {
    // Class for registering Firebending skills
    public static final FlameStepSkill FLAME_STEP_SKILL = new FlameStepSkill();
    public static final FireStrikeSkill FIRE_STRIKE_SKILL = new FireStrikeSkill();
//
//    public Firebending(String modId, String name) {
//        super(Avatar.MOD_ID, "firebending");
//    }

    public static void init() {}
}
