package com.amuzil.av3.bending.element.earth;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.api.magus.skill.data.SkillPathBuilder;
import com.amuzil.av3.api.magus.skill.traits.skilltraits.KnockbackTrait;
import com.amuzil.av3.api.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.av3.bending.skill.EarthSkill;
import com.amuzil.av3.capability.Bender;
import com.amuzil.av3.utils.Constants;

import static com.amuzil.av3.bending.form.BendingForms.RAISE;


public class EarthWallSkill extends EarthSkill {
    // TODO: Create Earth Wall Skill that adjusts shape based on player's look angle
    public EarthWallSkill() {
        super(Avatar.MOD_ID, "earth_wall");
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 1.5f));
        addTrait(new SizeTrait(Constants.SIZE, 1.0f));

        this.startPaths = SkillPathBuilder.getInstance()
                .add(RAISE)
                .build();
    }

    @Override
    public void start(Bender bender) {
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
