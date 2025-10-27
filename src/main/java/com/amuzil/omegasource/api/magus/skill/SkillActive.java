package com.amuzil.omegasource.api.magus.skill;

import com.amuzil.omegasource.api.magus.condition.conditions.EventCondition;
import com.amuzil.omegasource.api.magus.form.Form;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.event.SkillTickEvent;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.TimedTrait;
import com.amuzil.omegasource.capability.Bender;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;


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

    public SkillActive(String modID, String name) {
        super(modID, name);
    }

    @Override
    public List<Form> startPaths() {
        return startPaths;
    }

    @Override
    public List<Form> runPaths() {
        return runPaths;
    }

    @Override
    public List<Form> stopPaths() {
        return stopPaths;
    }

    @Override
    public boolean shouldRun(Bender bender, List<Form> formPath) {
        if (runPaths() == null) return false;
        SkillData skillData = bender.getSkillData(this);
        if (skillData == null) return false;
        return formPath.hashCode() == runPaths().hashCode();
    }

    @Override
    public boolean shouldStop(Bender bender, List<Form> formPath) {
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
        this.run = event -> {
            if (bender != null) {
                this.tick(bender);
            }
        };
        listen(SkillTickEvent.class, run);
    }

    @Override
    public void run(Bender bender) {
    }

    @Override
    public void stop(Bender bender) {
        SkillData data = bender.getSkillData(this);
        if (data != null) {
            data.setSkillState(SkillState.STOP);
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
