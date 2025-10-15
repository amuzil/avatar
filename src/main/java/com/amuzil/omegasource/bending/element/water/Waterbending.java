package com.amuzil.omegasource.bending.element.water;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.registry.Registries;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.bending.element.Element;
import net.minecraftforge.registries.RegistryObject;


public class Waterbending extends Element {
    // Class for registering Waterbending skills
    public static final RegistryObject<? extends Skill> WATER_BALL_SKILL = Registries.registerSkill("air_gust", WaterBallSkill::new);
    public static final RegistryObject<? extends Skill> WATER_STEP_SKILL = Registries.registerSkill("water_step", WaterStepSkill::new);

    public Waterbending() {
        super(Avatar.MOD_ID, "waterbending", Type.WATER);
    }

    public static void init() {}
}
