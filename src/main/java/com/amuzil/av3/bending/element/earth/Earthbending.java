package com.amuzil.av3.bending.element.earth;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.element.Element;
import com.amuzil.magus.registry.Registries;
import com.amuzil.magus.skill.Skill;

import java.util.function.Supplier;


public class Earthbending extends Element {
    // Class for registering Earthbending skills
    public static final Supplier<? extends Skill> EARTH_SMASH_SKILL = Registries.registerSkill(EarthSmashSkill::new);
    public static final Supplier<? extends Skill> EARTH_TOSS_SKILL = Registries.registerSkill(EarthTossSkill::new);
    public static final Supplier<? extends Skill> EARTH_BLOCK_SKILL = Registries.registerSkill(EarthBlockSkill::new);
    public static final Supplier<? extends Skill> EARTH_WALL_SKILL = Registries.registerSkill(EarthWallSkill::new);
    public static final Supplier<? extends Skill> EARTH_STEP_SKILL = Registries.registerSkill(EarthStepSkill::new);

    public Earthbending() {
        super(Avatar.MOD_ID, "earthbending", Type.EARTH);
    }

    public static void init() {
    }
}
