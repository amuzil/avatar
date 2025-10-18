package com.amuzil.omegasource.api.magus.skill;

import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.capability.Bender;
import net.minecraft.world.entity.LivingEntity;


public abstract class SkillActive extends Skill {

    public SkillActive(String modID, String name, SkillCategory category) {
        super(modID, name, category);
    }

    @Override
    public FormPath startPaths() {
        return startPaths;
    }

    @Override
    public FormPath runPaths() {
        return runPaths;
    }

    @Override
    public FormPath stopPaths() {
        return stopPaths;
    }

    @Override
    public boolean shouldStart(Bender bender, FormPath formPath) {
        return formPath.hashCode() == startPaths().hashCode();
    }

    @Override
    public boolean shouldRun(Bender bender, FormPath formPath) {
        if (runPaths() == null) return false;
        SkillData skillData = bender.getSkillData(this);
        if (skillData == null) return false;
        return formPath.hashCode() == runPaths().hashCode();
    }

    @Override
    public boolean shouldStop(Bender bender, FormPath formPath) {
        SkillData skillData = bender.getSkillData(this);
        if (skillData == null) return true;

        SkillState state = skillData.getSkillState();
        if(state == SkillState.STOP) return true;

        if (stopPaths() == null) return false;

        return formPath.hashCode() == stopPaths().hashCode();
    }

    @Override
    public void start(Bender bender) {
        bender.activeSkills.put(getSkillUuid(), this);
        listen();
    }

    @Override
    public void run(Bender bender) {
    }

    @Override
    public void stop(Bender bender) {
        SkillData data = bender.getSkillData(this);
        if(data != null) {
            data.setSkillState(SkillState.IDLE);
            bender.activeSkills.remove(getSkillUuid());
        }
        hush();
    }

    @Override
    public void reset(LivingEntity entity) {

    }


}
