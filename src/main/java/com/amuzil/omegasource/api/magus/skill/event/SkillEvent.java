package com.amuzil.omegasource.api.magus.skill.event;

import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.bending.form.ActiveForm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.List;


public class SkillEvent extends LivingEvent {

    private Skill skill;
    private List<ActiveForm> formPath;

    public SkillEvent(LivingEntity entity, List<ActiveForm> formPath, Skill skill) {
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

    public List<ActiveForm>  getFormPath() {
        return formPath;
    }
}
