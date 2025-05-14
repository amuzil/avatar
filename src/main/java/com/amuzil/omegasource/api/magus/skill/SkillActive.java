package com.amuzil.omegasource.api.magus.skill;

import com.amuzil.omegasource.api.magus.form.FormPath;
import net.minecraft.world.entity.LivingEntity;


public class SkillActive extends Skill {

    public SkillActive(String modID, String name, SkillCategory category) {
        super(modID, name, category);
    }

    @Override
    public FormPath getStartPaths() {
        return startPaths;
    }

    @Override
    public FormPath getRunPaths() {
        return runPaths;
    }

    @Override
    public FormPath getStopPaths() {
        return stopPaths;
    }

    @Override
    public boolean shouldStart(LivingEntity entity, FormPath formPath) {
        return formPath.hashCode() == getStartPaths().hashCode();
    }

    @Override
    public boolean shouldRun(LivingEntity entity, FormPath formPath) {
        if (getRunPaths() == null)
            return false;
        return formPath.hashCode() == getRunPaths().hashCode();
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
    public void reset(LivingEntity entity, FormPath formPath) {

    }


}
