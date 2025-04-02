package com.amuzil.omegasource.api.magus.skill.event;

import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.Skill;
import net.minecraft.world.entity.LivingEntity;


public class SkillTickEvent extends SkillEvent {

    public SkillTickEvent(LivingEntity entity, FormPath formPath, Skill skill) {
        super(entity, formPath, skill);
    }

    public static class Start extends SkillTickEvent {

        public Start(LivingEntity entity, FormPath formPath, Skill skill) {
            super(entity, formPath, skill);
        }
    }

    public static class Run extends SkillTickEvent {

        public Run(LivingEntity entity, FormPath formPath, Skill skill) {
            super(entity, formPath, skill);
        }
    }

    public static class Stop extends SkillTickEvent {

        public Stop(LivingEntity entity, FormPath formPath, Skill skill) {
            super(entity, formPath, skill);
        }
    }


}
