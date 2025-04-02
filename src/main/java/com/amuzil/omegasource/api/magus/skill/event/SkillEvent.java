package com.amuzil.omegasource.api.magus.skill.event;

import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.Skill;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;


public class SkillEvent extends LivingEvent {

    private Skill skill;
    private FormPath formPath;

    public SkillEvent(LivingEntity entity, FormPath formPath, Skill skill) {
        super(entity);
        this.skill = skill;
        this.formPath = formPath;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    public Skill getSkill() {
        return skill;
    }

    public FormPath  getFormPath() {
        return formPath;
    }
}
