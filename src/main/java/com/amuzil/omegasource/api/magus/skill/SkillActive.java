package com.amuzil.omegasource.api.magus.skill;

import com.amuzil.omegasource.api.magus.condition.conditions.EventCondition;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.event.SkillTickEvent;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.TimedTrait;
import com.amuzil.omegasource.capability.Bender;
import net.minecraft.world.entity.LivingEntity;


public abstract class SkillActive extends Skill {

    protected final EventCondition<SkillTickEvent> cdr = new EventCondition<>(SkillTickEvent.class, event -> {
        SkillData data = bender.getSkillData(name());
        if (data == null) return false;
        TimedTrait cd = data.getTrait("cooldown", TimedTrait.class);
        if (cd == null)
            return false;
        if (cd.getTime() > 0) {
            cd.setTime(cd.getTime() - 1);
        }
        return cd.getTime() > 0;
    });

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
        SkillData skillData = bender.getSkillData(this);
        if (skillData == null) return false;
        SkillState state = skillData.getSkillState();
        System.out.println("state " + state);
        if (state == SkillState.RUN) return true;
        if (runPaths() == null) return false;
        return formPath.hashCode() == runPaths().hashCode();
    }

    @Override
    public boolean shouldStop(Bender bender, FormPath formPath) {
        SkillData skillData = bender.getSkillData(this);
        if (skillData == null) return true;
        SkillState state = skillData.getSkillState();
        if (state == SkillState.STOP) return true;
        if (stopPaths() == null) return false;
        return formPath.hashCode() == stopPaths().hashCode();
    }

    @Override
    public void start(Bender bender) {
        bender.activeSkills.put(getUUID(), this);

        // Initialises the skill thread runnable
        this.run = event -> {
            this.tick(bender);
        };

        // Registers the skill thread runnable to the SkillTickEvent
        listen(SkillTickEvent.class, run);
    }

    @Override
    public void run(Bender bender) {
    }

    @Override
    public void stop(Bender bender) {
        hush(run);
        SkillData data = bender.getSkillData(this);
        if (data != null) {
            data.setSkillState(SkillState.IDLE);
            bender.activeSkills.remove(getUUID());
        }
    }

    @Override
    public void reset(LivingEntity entity) {

    }

    protected void cooldown() {
        SkillData persistentData = bender.getSkillData(name());
        persistentData.getTrait("cooldown", TimedTrait.class).setTime(
                persistentData.getTrait("max_cooldown", TimedTrait.class).getTime()
        );
        cdr.registerRunnables("cooldown", () -> {
        }, cdr::unregister);
    }
}
