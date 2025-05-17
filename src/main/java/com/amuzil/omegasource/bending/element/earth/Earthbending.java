package com.amuzil.omegasource.bending.element.earth;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.bending.element.Element;


public class Earthbending extends Element {
    // Class for registering Earthbending skills
    public static final EarthTossSkill EARTH_TOSS_SKILL = new EarthTossSkill();
    public static final EarthBlockSkill EARTH_BLOCK_SKILL = new EarthBlockSkill();

    public Earthbending() {
        super(Avatar.MOD_ID, "earthbending");
    }

    public static void init() {}
}
