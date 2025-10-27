package com.amuzil.omegasource.bending;

import com.amuzil.omegasource.api.magus.skill.SkillActive;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.LevelTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.TimedTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.XPTrait;
import com.amuzil.omegasource.bending.element.Element;


public abstract class BendingSkill extends SkillActive {

    public BendingSkill(String modID, String name) {
        super(modID, name);
        addTrait(new XPTrait("xp", 0));
        addTrait(new LevelTrait("level", 0));
        addTrait(new LevelTrait("tier", 1));
        addTrait(new TimedTrait("max_cooldown", 40));
        addTrait(new TimedTrait("cooldown", 40));
    }

    public Element element() {
        return (Element) getCategory();
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

    // Increment TimedTrait until maxTime is reached, then reset to 0 and set skill state to IDLE
    public void incrementTimedTrait(SkillData data, String name, int maxTime) {
        TimedTrait time = data.getTrait(name, TimedTrait.class);
        if (time.getTime() < maxTime) {
            time.setTime(time.getTime() + 1);
        } else {
            time.setTime(0);
            data.setSkillState(SkillState.IDLE);
        }
    }


}
