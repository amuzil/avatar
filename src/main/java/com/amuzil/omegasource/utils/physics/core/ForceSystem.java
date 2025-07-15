package com.amuzil.omegasource.utils.physics.core;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;


public class ForceSystem {

    // Used to determine what to run on. We want both client and server-side annotations, if possible.
    Dist side;

    public Dist side() {
        return this.side;
    }

    private List<ForceEmitter> emitters;

    public void tick(double dt) {
        for (ForceEmitter e : emitters) {
            e.tick(dt);
        }
    }
}
