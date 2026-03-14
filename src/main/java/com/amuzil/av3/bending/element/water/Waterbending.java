package com.amuzil.av3.bending.element.water;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.element.Element;
import com.amuzil.magus.registry.Registries;
import com.amuzil.magus.skill.Skill;

import java.util.function.Supplier;

public class Waterbending extends Element {
    // Class for registering Waterbending skills
    public static final Supplier<? extends Skill> WATER_BALL_SKILL = Registries.registerSkill(WaterBallSkill::new);
    public static final Supplier<? extends Skill> WATER_STEP_SKILL = Registries.registerSkill(WaterStepSkill::new);
//    public static final Supplier<? extends Skill> WATER_RING_SKILL = Registries.registerSkill(WaterRingSkill::new);
//    public static final Supplier<? extends Skill> WATER_STREAM_SKILL = Registries.registerSkill(WaterStreamSkill::new);
//    public static final Supplier<? extends Skill> WATER_ARC_SKILL = Registries.registerSkill(WaterArcSkill::new);
//    public static final Supplier<? extends Skill> WATER_WALL_SKILL = Registries.registerSkill(WaterWallSkill::new);
//    public static final Supplier<? extends Skill> WATER_TENTACLE_SKILL = Registries.registerSkill(WaterTentacleSkill::new);
//    public static final Supplier<? extends Skill> WATER_SPHERE_SKILL = Registries.registerSkill(WaterSphereSkill::new);
//    public static final Supplier<? extends Skill> WATER_BURST_SKILL = Registries.registerSkill(WaterBurstSkill::new);
    public static final Supplier<? extends Skill> WATER_SHIELD_SKILL = Registries.registerSkill(WaterShieldSkill::new);

    public Waterbending() {
        super(Avatar.MOD_ID, "waterbending", Type.WATER);
    }

    public static void init() {
    }
}
