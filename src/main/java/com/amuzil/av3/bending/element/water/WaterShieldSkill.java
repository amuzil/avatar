package com.amuzil.av3.bending.element.water;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.skill.WaterSkill;
import com.amuzil.av3.utils.Constants;
import com.amuzil.magus.skill.traits.SkillTrait;
import com.amuzil.magus.skill.traits.skilltraits.FloatTrait;
import com.amuzil.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.magus.skill.traits.skilltraits.TimedTrait;
import org.checkerframework.checker.units.qual.C;

// Creates a shield of water around the player that can be used to block incoming attacks. The shield will have a certain amount of health and will break after
// taking enough damage.
// The shield can also be used to block projectiles, and will have a cooldown before it can be used again.
public class WaterShieldSkill extends WaterSkill {

    public WaterShieldSkill() {
        super(Avatar.MOD_ID, "water_shield");
        addTrait(new SizeTrait(Constants.SIZE, 0.75f));
        addTrait(new FloatTrait(Constants.HEALTH, 5.0f));
        addTrait(new TimedTrait(Constants.LIFETIME,  120));
        addTrait(new TimedTrait(Constants.COOLDOWN, 200));
    }


}
