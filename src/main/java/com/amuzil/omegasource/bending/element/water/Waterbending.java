package com.amuzil.omegasource.bending.element.water;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.bending.element.earth.EarthStepSkill;


public class Waterbending extends Element {
    // Class for registering Waterbending skills
    public static final WaterBallSkill WATER_BALL_SKILL = new WaterBallSkill();
    public static final WaterStepSkill WATER_STEP_SKILL = new WaterStepSkill();

    public Waterbending() {
        super(Avatar.MOD_ID, "waterbending", Type.WATER);
    }

    public static void init() {}
}
