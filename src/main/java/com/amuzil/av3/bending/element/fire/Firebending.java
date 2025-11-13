package com.amuzil.av3.bending.element.fire;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.element.Element;
import com.amuzil.av3.bending.element.fire.forms.FirePushForm;
import com.amuzil.magus.registry.Registries;
import com.amuzil.magus.skill.Skill;

import java.util.function.Supplier;


public class Firebending extends Element {
    // Class for registering Firebending skills
    public static final Supplier<? extends Skill> FIRE_STRIKE_SKILL = Registries.registerSkill(FireStrikeSkill::new);
    public static final Supplier< ? extends Skill>FIRE_STEP_SKILL = Registries.registerSkill(FlameStepSkill::new);
    public static final Supplier<? extends Skill> FLAME_STREAM_SKILL = Registries.registerSkill(FlameStreamSkill::new);
    public static final Supplier<? extends Skill> BLAZING_RINGS_SKILL = Registries.registerSkill(BlazingRingsSkill::new);

    // Class for registering Firebending forms
    public static final Supplier<? extends Skill> FIRE_PUSH_FORM = Registries.registerSkill(FirePushForm::new);


    public Firebending() {
        super(Avatar.MOD_ID, "firebending", Type.FIRE);
    }

    public static void init() {
    }
}
