package com.amuzil.omegasource.api.magus.skill;

import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.capability.Bender;
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
    public boolean shouldStart(Bender bender, FormPath formPath) {
        return formPath.hashCode() == getStartPaths().hashCode();
    }

    @Override
    public boolean shouldRun(Bender bender, FormPath formPath) {
        if (getRunPaths() == null)
            return false;
        return formPath.hashCode() == getRunPaths().hashCode();
    }

    @Override
    public boolean shouldStop(Bender bender, FormPath formPath) {
        if (getStopPaths() == null)
            return false;
        return formPath.hashCode() == getStopPaths().hashCode();
    }

    @Override
    public void start(Bender bender) {

    }

    @Override
    public void run(Bender bender) {

    }

    @Override
    public void stop(Bender bender) {
        SkillData data = bender.getSkillData(this);
        data.setSkillState(SkillState.IDLE);
    }

    @Override
    public void reset(LivingEntity entity) {

    }


}
