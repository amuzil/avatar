package com.amuzil.omegasource.bending.element.fire;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.registry.Registries;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.bending.element.Element;
import net.minecraftforge.registries.RegistryObject;


public class Firebending extends Element {
    // Class for registering Firebending skills
    public static final RegistryObject<? extends Skill> FIRE_STRIKE_SKILL = Registries.registerSkill("fire_strike", FireStrikeSkill::new);
    public static final RegistryObject< ? extends Skill> FIRE_STEP_SKILL = Registries.registerSkill("flame_step", FlameStepSkill::new);
    public static final RegistryObject<? extends Skill> FLAME_STREAM_SKILL = Registries.registerSkill("flame_stream", FlameStreamSkill::new);
    public static final RegistryObject<? extends Skill> BLAZING_RINGS_SKILL = Registries.registerSkill("blazing_rings", BlazingRingsSkill::new);

    public Firebending() {
        super(Avatar.MOD_ID, "firebending", Type.FIRE);
    }

    public static void init() {}
}
