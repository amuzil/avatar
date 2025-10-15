package com.amuzil.omegasource.bending.element.fire;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.registry.Registries;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.bending.element.earth.EarthQuakeSkill;
import com.amuzil.omegasource.entity.AvatarEntity;
import net.minecraftforge.registries.RegistryObject;


public class Firebending extends Element {
    // Class for registering Firebending skills
//    public static final FireStrikeSkill FIRE_STRIKE_SKILL = new FireStrikeSkill();
//    public static final FlameStepSkill FLAME_STEP_SKILL = new FlameStepSkill();
//    public static final FlameStreamSkill FLAME_STREAM_SKILL = new FlameStreamSkill();
//    public static final BlazingRingsSkill BLAZING_RINGS_SKILL = new BlazingRingsSkill();

    public static final RegistryObject<? extends Skill> FIRE_STRIKE_SKILL = Registries.registerSkill("fire_strike", FireStrikeSkill::new);
    public static final RegistryObject< ? extends Skill> FIRE_STEP_SKILL = Registries.registerSkill("flame_step", FlameStepSkill::new);
    public static final RegistryObject<? extends Skill> FLAME_STREAM_SKILL = Registries.registerSkill("flame_stream", FlameStreamSkill::new);
    public static final RegistryObject<? extends Skill> BLAZING_RINGS_SKILL = Registries.registerSkill("blazing_rings", BlazingRingsSkill::new);

    public Firebending() {
        super(Avatar.MOD_ID, "firebending", Type.FIRE);
    }

    public static void init() {}
}
