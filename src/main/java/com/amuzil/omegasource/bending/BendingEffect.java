package com.amuzil.omegasource.bending;

import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.skill.utils.data.SkillPathBuilder;
import net.minecraft.world.entity.LivingEntity;

public class BendingEffect extends BendingSkill {

    public BendingEffect(String modID, String name, SkillCategory category) {
        super(modID, name, category);
    }

    @Override
    public boolean shouldStart(LivingEntity entity, FormPath formPath) {
        return SkillPathBuilder.checkForms(formPath.simple(), getStartPaths().simple());
    }
}
