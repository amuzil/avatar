package com.amuzil.av3.bending;

import com.amuzil.av3.bending.element.Element;
import com.amuzil.magus.skill.SkillActive;
import com.amuzil.magus.skill.data.SkillData;
import com.amuzil.magus.skill.traits.skilltraits.TimedTrait;


public abstract class BendingSkill extends SkillActive {

    public BendingSkill(String modID, String name) {
        super(modID, name);
    }

    public Element element() {
        return (Element) getCategory();
    }

    // Ensure SkillState gets set to RUN before super.start() is called so run() executes on 1st try
    public void  startRun() {
        skillData.setSkillState(SkillState.RUN);
        super.start(bender);
    }

    public void stopRun() {
        skillData.setSkillState(SkillState.STOP);
        this.stop(bender);
    }

    // TODO: Update cooldown methods to use cooldown runnable in Skill
    public void resetCooldown(SkillData data) {
        TimedTrait trait = data.getTrait("cooldown", TimedTrait.class);
        assert trait != null;
        trait.setTime(data.getTrait("max_cooldown", TimedTrait.class).getTime());
    }

    /**
     * @return Whether the current cooldown for a skill is ready.
     */
    public boolean checkCooldown() {
        SkillData data = bender.getSkillData(name());
        TimedTrait time = data.getTrait("cooldown", TimedTrait.class);
        return time != null && time.getTime() <= 0;
    }

    // Increment TimedTrait until maxTime is reached, then reset to 0 and set skill state to STOP
    public void incrementTimedTrait(SkillData data, String name, int maxTime) {
        TimedTrait time = data.getTrait(name, TimedTrait.class);
        if (time.getTime() < maxTime) {
            time.setTime(time.getTime() + 1);
        } else {
            time.setTime(0);
            data.setSkillState(SkillState.STOP);
        }
    }

}
