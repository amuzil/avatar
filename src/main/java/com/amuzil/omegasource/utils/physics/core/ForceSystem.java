package com.amuzil.omegasource.utils.physics.core;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;


public class ForceSystem {

    // Used to determine what to run on. We want both client and server-side annotations, if possible.
    Dist side;

    //TODO: Change this to use a double SpatialGrid approach. One grid stores ForceClouds at the system level,
    // and checks what to compare against. The other adds in data at the ForcePoint level, and is used for finer
    // collisions or visuals.

    public Dist side() {
        return this.side;
    }
    private ForceGrid<ForceCloud> cloudGrid;

    private List<ForceEmitter> emitters;

    public void tick(double dt) {
        for (ForceEmitter e : emitters) {
            e.tick(dt);
        }
    }
}
