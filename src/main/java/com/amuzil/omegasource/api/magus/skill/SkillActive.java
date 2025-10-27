package com.amuzil.omegasource.api.magus.skill;

import com.amuzil.omegasource.api.magus.condition.conditions.EventCondition;
import com.amuzil.omegasource.api.magus.form.Form;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.event.SkillTickEvent;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.LevelTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.TimedTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.XPTrait;
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
        addTrait(new XPTrait("xp", 0));
        addTrait(new LevelTrait("level", 0));
        addTrait(new LevelTrait("tier", 1));
        addTrait(new TimedTrait("max_cooldown", 40));
        addTrait(new TimedTrait("cooldown", 40));
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
        SkillData skillData = bender.getSkillData(this);
        if (skillData == null) return false;
        SkillState state = skillData.getSkillState();
        if (state == SkillState.RUN) return true;
        if (runPaths() == null) return false;
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
    public void start(Bender bender) { // If using run method, call this super last or after setting SkillState to RUN!
        bender.activeSkills.put(getUUID(), this);

        // Initialises the skill thread runnable
        this.run = event -> {
            this.tick(bender);
        };

        // Registers the skill thread runnable to the SkillTickEvent
        listen(SkillTickEvent.class, run);
    }

    @Override
    public void run(Bender bender) { // If you want your Skill to run per tick don't call this super, just override!
        // We should not run tick listener by default for every Skill because this causes a runaway tick runnable for
        // one shot Skills that only use start() method. In other words, every skill would need to define or specify
        // how their skill shouldStop just to remove the listener.
        if (!shouldRun(bender, bender.formPath))
            stop(bender);
    }

    @Override
    public void stop(Bender bender) {
        bender.activeSkills.remove(getUUID());
        hush(run);
        if (skillData != null)
            skillData.setSkillState(SkillState.IDLE);
    }

    @Override
    public void reset(LivingEntity entity) {
        if (skillData != null) {
            skillData.setSkillState(SkillState.IDLE);
        }
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
