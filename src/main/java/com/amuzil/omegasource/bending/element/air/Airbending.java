package com.amuzil.omegasource.bending.element.air;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.registry.Registries;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.bending.element.Element;
import net.minecraftforge.registries.RegistryObject;


public class Airbending extends Element {
    // Class for registering Airbending skills
    public static final RegistryObject<? extends Skill> AIR_GUST_SKILL = Registries.registerSkill(AirGustSkill::new);
    public static final RegistryObject<? extends Skill> AIR_STEP_SKILL = Registries.registerSkill(AirStepSkill::new);
    public static final RegistryObject<? extends Skill> AIR_PULL_SKILL = Registries.registerSkill(AirPullSkill::new);
    public static final RegistryObject<? extends Skill> AIR_SWIPE_SKILL = Registries.registerSkill(AirSwipeSkill::new);

    public Airbending() {
        super(Avatar.MOD_ID, "airbending", Type.AIR);
    }

    public static void init() {}
}
