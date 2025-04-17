package com.amuzil.omegasource.api.magus.skill;

import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.capability.entity.Magi;
import com.amuzil.omegasource.api.magus.skill.utils.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.utils.data.SkillPathBuilder;
import net.minecraft.world.entity.LivingEntity;

public class SkillActive extends Skill {

    public SkillActive(String modID, String name, SkillCategory category) {
        super(modID, name, category);
    }

    @Override
    public FormPath getStartPaths() {
        return null;
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
        Magi magi = Magi.get(entity);
       
        return formPath.hashCode() == getStartPaths().hashCode();
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

//        Magi.get(entity).formPath.clear();
    }

    @Override
    public void run(LivingEntity entity) {
    }

    @Override
    public void stop(LivingEntity entity) {
        Magi magi = Magi.get(entity);
        if (magi != null) {
            SkillData data = magi.getSkillData(this);
            data.setState(SkillState.IDLE);
        }
    }

    @Override
    public void reset(LivingEntity entity, FormPath formPath) {

    }
}
