package com.amuzil.omegasource.bending.element.earth;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.registry.Registries;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.bending.element.Element;
import net.minecraftforge.registries.RegistryObject;


public class Earthbending extends Element {
    // Class for registering Earthbending skills
    public static final EarthTossSkill EARTH_TOSS_SKILL = new EarthTossSkill();
    public static final EarthBlockSkill EARTH_BLOCK_SKILL = new EarthBlockSkill();
    public static final EarthStepSkill EARTH_STEP_SKILL = new EarthStepSkill();

    public static final RegistryObject<? extends Skill> EARTH_QUAKE_SKILL = Registries.registerSkill("earth_quake", EarthQuakeSkill::new);

    public Earthbending() {
        super(Avatar.MOD_ID, "earthbending", Type.EARTH);
    }

    public static void init() {}
}
