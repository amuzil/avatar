package com.amuzil.av3.bending.element.water;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.skill.WaterSkill;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.utils.Constants;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.traits.skilltraits.FloatTrait;
import com.amuzil.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.magus.skill.traits.skilltraits.StringTrait;
import com.amuzil.magus.skill.traits.skilltraits.TimedTrait;

import static com.amuzil.av3.bending.form.BendingForms.BLOCK;

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
        addTrait(new StringTrait(Constants.FX, "water_shield"));

        startPaths = SkillPathBuilder.getInstance().add(BLOCK).build();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);


    }

    @Override
    public void run(Bender bender) {
        super.run(bender);
    }

    @Override
    public void stop(Bender bender) {
        super.stop(bender);
    }
}
