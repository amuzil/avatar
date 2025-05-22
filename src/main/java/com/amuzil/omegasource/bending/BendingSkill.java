package com.amuzil.omegasource.bending;

import com.amuzil.omegasource.api.magus.skill.SkillActive;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.LevelTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.TimedTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.XPTrait;


public class BendingSkill extends SkillActive {

    public BendingSkill(String modID, String name, SkillCategory category) {
        super(modID, name, category);
        addTrait(new XPTrait(0, "xp"));
        addTrait(new LevelTrait(0, "level"));
        addTrait(new LevelTrait(1, "tier"));
        addTrait(new TimedTrait(40, "max_cooldown"));
        addTrait(new TimedTrait(40, "cooldown"));
    }

    public void resetCooldown(SkillData data) {
        TimedTrait trait = data.getTrait("cooldown", TimedTrait.class);
        assert trait != null;
        trait.setTime(data.getTrait("max_cooldown", TimedTrait.class).getTime());
    }

    public boolean checkCooldown(SkillData data) {
        TimedTrait time = data.getTrait("cooldown", TimedTrait.class);
        if (time.getTime() > 0) {
            time.setTime(time.getTime() - 1);
            return false;
        }
        else {
            data.setState(SkillState.START);
            return true;
        }
    }
}
