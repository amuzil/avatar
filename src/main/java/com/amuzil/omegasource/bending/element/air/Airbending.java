package com.amuzil.omegasource.bending.element.air;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.bending.element.Element;
import org.checkerframework.checker.units.qual.A;


public class Airbending extends Element {
    // Class for registering Airbending skills
    public static final AirGustSkill AIR_GUST_SKILL = new AirGustSkill();
    public static final AirStepSkill AIR_STEP_SKILL = new AirStepSkill();

    public Airbending() {
        super(Avatar.MOD_ID, "airbending");
    }

    public static void init() {}
}
