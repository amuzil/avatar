package com.amuzil.omegasource.api.magus.skill;

import com.amuzil.omegasource.api.magus.skill.utils.data.SkillPathBuilder;
import com.amuzil.omegasource.bending.form.ActiveForm;
import net.minecraft.world.entity.LivingEntity;

public class SkillActive extends Skill{

    public SkillActive(String modID, String name, SkillCategory category) {
        super(modID, name, category);
    }

    @Override
    public FormPath getStartPaths() {
        return SkillPathBuilder.getInstance().complexForm(new ActiveForm());
    }

    @Override
    public FormPath getRunPaths() {
        return null;
    }

    @Override
    public FormPath getStopPaths() {
        return null;
    }

    @Override
    public boolean shouldStart(LivingEntity entity, FormPath formPath) {
        return false;
    }

    @Override
    public boolean shouldRun(LivingEntity entity, FormPath formPath) {
        return false;
    }

    @Override
    public boolean shouldStop(LivingEntity entity, FormPath formPath) {
        return false;
    }

    @Override
    public void start(LivingEntity entity) {

    }

    @Override
    public void run(LivingEntity entity) {

    }

    @Override
    public void stop(LivingEntity entity) {

    }

    @Override
    public void reset(LivingEntity entity, FormPath formPath) {

    }
}
