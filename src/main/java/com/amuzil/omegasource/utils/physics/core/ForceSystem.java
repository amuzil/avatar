package com.amuzil.omegasource.utils.physics.core;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public class ForceSystem {

    // Used to determine what to run on. We want both client and server-side annotations, if possible.
    Dist side;

    public Dist side() {
        return this.side;
    }

}
