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

    public ForceSystem(double systemCellSize) {
        this.cloudGrid = new ForceGrid<>(systemCellSize);
    }

    public void addEmitter(ForceEmitter e) {
        if (!emitters.contains(e)) {
            emitters.add(e);
        }
    }

    public void unregisterEmitter(ForceEmitter e) {
        emitters.remove(e);
        // Optionally remove that emitter's clouds from the grid
        for (ForceCloud c : e.getClouds()) {
            cloudGrid.remove(c, c.pos());
        }
    }

    public void tick(double dt) {
        for (ForceEmitter e : emitters) {
            e.tick(dt);
        }
        // Need to rebuild the cloud grid each tick, as clouds may have moved

        
    }
}

