package com.amuzil.omegasource.bending;

import com.amuzil.omegasource.api.magus.skill.SkillCategory;


@Deprecated
public abstract class BendingEffect extends BendingSkill {

    public BendingEffect(String modID, String name) {
        super(modID, name);
    }

//    @Override
//    public boolean shouldStart(LivingEntity entity, FormPath formPath) {
//        boolean shouldStart = false;
//        Bender bender = (Bender) Bender.getBender(entity);
//        if (bender != null) {
//            SkillData data = bender.getSkillData(this);
//            if (data.getSkillState().equals(SkillState.START)) {
//                shouldStart = true;
//            } else if (data.getSkillState().equals(SkillState.IDLE)) {
//                shouldStart = checkCooldown(data);
//            } else if (data.getSkillState().equals(SkillState.RUN)) {
//                shouldStart = checkCooldown(data);
//            }
//        }
//        return super.shouldStart(entity, formPath) && shouldStart;
//    }
}
