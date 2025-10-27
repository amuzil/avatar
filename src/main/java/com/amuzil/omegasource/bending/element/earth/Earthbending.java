package com.amuzil.omegasource.bending.element.earth;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.registry.Registries;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.bending.element.Element;
import net.minecraftforge.registries.RegistryObject;


public class Earthbending extends Element {
    // Class for registering Earthbending skills
    public static final RegistryObject<? extends Skill> EARTH_QUAKE_SKILL = Registries.registerSkill(EarthQuakeSkill::new);
    public static final RegistryObject<? extends Skill> EARTH_TOSS_SKILL = Registries.registerSkill(EarthTossSkill::new);
    public static final RegistryObject<? extends Skill> EARTH_BLOCK_SKILL = Registries.registerSkill(EarthBlockSkill::new);
    public static final RegistryObject<? extends Skill> EARTH_WALL_SKILL = Registries.registerSkill(EarthWallSkill::new);
    public static final RegistryObject<? extends Skill> EARTH_STEP_SKILL = Registries.registerSkill(EarthStepSkill::new);

    public Earthbending() {
        super(Avatar.MOD_ID, "earthbending", Type.EARTH);
    }

    public static void init() {
    }
}
