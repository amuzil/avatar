package com.amuzil.omegasource.bending;

import com.amuzil.omegasource.api.magus.capability.entity.Magi;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import net.minecraft.world.entity.LivingEntity;

public class BendingEffect extends BendingSkill {

    public BendingEffect(String modID, String name, SkillCategory category) {
        super(modID, name, category);
    }

    @Override
    public boolean shouldStart(LivingEntity entity, FormPath formPath) {
        boolean shouldStart = false;
        Magi magi = Magi.get(entity);
        if (magi != null) {
            SkillData data = magi.getSkillData(this);
            if (data.getState().equals(SkillState.START)) {
                shouldStart = true;
            }
            else if (data.getState().equals(SkillState.IDLE)) {
                shouldStart = checkCooldown(data);
            }
        }

        return super.shouldStart(entity, formPath) && shouldStart;
    }
}
