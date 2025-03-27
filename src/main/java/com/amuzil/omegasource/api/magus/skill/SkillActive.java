package com.amuzil.omegasource.api.magus.skill;

import com.amuzil.omegasource.api.magus.radix.RadixTree;
import com.amuzil.omegasource.api.magus.skill.utils.capability.entity.Magi;
import com.amuzil.omegasource.api.magus.skill.utils.data.SkillPathBuilder;
import com.amuzil.omegasource.bending.form.ActiveForm;
import com.amuzil.omegasource.bending.form.Forms;
import net.minecraft.world.RandomSequence;
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
        boolean selected = magi.currentlySelected() != null && magi.currentlySelected().equals(this);
        RadixTree.getLogger().debug("Form Path: " + formPath.complex() + ", Start Path: " + getStartPaths().complex());
        return SkillPathBuilder.checkForms(formPath.complex(), getStartPaths().complex()) ||
                SkillPathBuilder.checkForms(formPath.simple(), getStartPaths().simple()) && selected;
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
        System.out.println("Success!");
        Magi.get(entity).formPath.clear();
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
