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
        if (runPaths() == null)
            return false;
        return formPath.hashCode() == runPaths().hashCode();
    }

    @Override
    public boolean shouldStop(Bender bender, FormPath formPath) {
        if (stopPaths() == null)
            return false;
        SkillState state = bender.getSkillData(this).getSkillState();
        return state != SkillState.IDLE && formPath.hashCode() == stopPaths().hashCode();
    }

    @Override
    public void start(Bender bender) {
        bender.activeSkills.put(getSkillUuid(), this);
        if (shouldRun(bender, bender.formPath))
            listen();
    }

    @Override
    public void run(Bender bender) {
        if (shouldStop(bender, bender.formPath) || !shouldRun(bender, bender.formPath)) {
            SkillData data = bender.getSkillData(this);
            data.setSkillState(SkillState.STOP);
            stop(bender);
            hush();
        }

    }

    @Override
    public void stop(Bender bender) {
        SkillData data = bender.getSkillData(this);
        data.setSkillState(SkillState.IDLE);
        bender.activeSkills.remove(getSkillUuid());
        hush();
    }

    @Override
    public void reset(LivingEntity entity) {

    }


}
