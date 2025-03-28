package com.amuzil.omegasource.api.magus.skill;

import com.amuzil.omegasource.api.magus.radix.RadixTree;
import com.amuzil.omegasource.api.magus.skill.utils.capability.entity.Magi;
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
       
        return SkillPathBuilder.checkForms(formPath.complex(), getStartPaths().complex());
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
        if (!entity.level().isClientSide)
            RadixTree.getLogger().info("Server Success! ");
        else RadixTree.getLogger().info("Client Success! ");
//        Magi.get(entity).formPath.clear();
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
