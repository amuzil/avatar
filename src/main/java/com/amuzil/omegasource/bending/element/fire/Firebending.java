package com.amuzil.omegasource.bending.element.fire;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.registry.Registries;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.bending.element.fire.forms.FirePushForm;
import com.amuzil.omegasource.bending.element.fire.skills.BlazingRingsSkill;
import com.amuzil.omegasource.bending.element.fire.skills.FireStrikeSkill;
import com.amuzil.omegasource.bending.element.fire.skills.FlameStepSkill;
import com.amuzil.omegasource.bending.element.fire.skills.FlameStreamSkill;
import net.minecraftforge.registries.RegistryObject;


public class Firebending extends Element {
    // Class for registering Firebending skills
    public static final RegistryObject<? extends Skill> FIRE_STRIKE_SKILL = Registries.registerSkill(FireStrikeSkill::new);
    public static final RegistryObject< ? extends Skill>FIRE_STEP_SKILL = Registries.registerSkill(FlameStepSkill::new);
    public static final RegistryObject<? extends Skill> FLAME_STREAM_SKILL = Registries.registerSkill(FlameStreamSkill::new);
    public static final RegistryObject<? extends Skill> BLAZING_RINGS_SKILL = Registries.registerSkill(BlazingRingsSkill::new);

    // Class for registering Firebending forms
    public static final RegistryObject<? extends Skill> FIRE_PUSH_FORM = Registries.registerSkill(FirePushForm::new);


    public Firebending() {
        super(Avatar.MOD_ID, "firebending", Type.FIRE);
    }

    public static void init() {
    }
}
