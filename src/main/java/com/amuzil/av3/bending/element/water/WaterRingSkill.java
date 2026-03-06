package com.amuzil.av3.bending.element.water;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.skill.WaterSkill;
import com.amuzil.av3.data.capability.Bender;

/**
 * Makes a consumable ring of water around the player that can be used to source other water skills - primarily the ball, stream, and arc skills.
 */
public class WaterRingSkill extends WaterSkill {

    public WaterRingSkill() {
        super(Avatar.MOD_ID, "water_ring");
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
