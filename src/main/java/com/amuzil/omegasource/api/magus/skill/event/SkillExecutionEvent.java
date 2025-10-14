package com.amuzil.omegasource.api.magus.skill.event;

import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.Skill;
import net.minecraft.world.entity.LivingEntity;


public class SkillExecutionEvent extends SkillEvent {

    public SkillExecutionEvent(LivingEntity entity, FormPath formPath, Skill skill) {
        super(entity, formPath, skill);
    }

    public static class Start extends SkillExecutionEvent {

        public Start(LivingEntity entity, FormPath formPath, Skill skill) {
            super(entity, formPath, skill);
        }
    }

    public static class Run extends SkillExecutionEvent {

        public Run(LivingEntity entity, FormPath formPath, Skill skill) {
            super(entity, formPath, skill);
        }
    }

    public static class Stop extends SkillExecutionEvent {

        public Stop(LivingEntity entity, FormPath formPath, Skill skill) {
            super(entity, formPath, skill);
        }
    }


}
