package com.amuzil.omegasource.api.magus.skill;

import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.event.SkillTickEvent;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.TimedTrait;
import com.amuzil.omegasource.capability.Bender;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.function.Consumer;


public abstract class SkillActive extends Skill {

    private final Consumer<SkillTickEvent> cooldown = event -> {;
        // We want to change persistent data, not instanced data.
        SkillData data = bender.getSkillData(name());
        if (data == null) return;
        TimedTrait cooldown = data.getTrait("cooldown", TimedTrait.class);
        if (cooldown == null)
            return;
        if (cooldown.getTime() > 0) {
            cooldown.setTime(cooldown.getTime() - 1);
        }
    };

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

    public void startCooldown() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, SkillTickEvent.class, cooldown);
    }

    public void stopCooldown() {
        MinecraftForge.EVENT_BUS.unregister(cooldown);
    }

}
