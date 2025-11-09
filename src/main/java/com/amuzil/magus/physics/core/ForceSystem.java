package com.amuzil.magus.physics.core;

import net.neoforged.api.distmarker.Dist;

import java.util.List;


public class ForceSystem {

    // Used to determine what to run on. We want both client and server-side annotations, if possible.
    Dist side;

    //TODO: Change this to use a double SpatialGrid approach. One grid stores ForceClouds at the system level,
    // and checks what to compare against. The other adds in data at the ForcePoint level, and is used for finer
    // collisions or visuals.
    private final ForceGrid<ForceCloud> cloudGrid;
    private List<ForceEmitter> emitters;

    public ForceSystem(double systemCellSize) {
        this.cloudGrid = new ForceGrid<>(systemCellSize);
    }

    public Dist side() {
        return this.side;
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

    // Go over grid each tick. Maybe skip emitters?
    public void tick(double dt) {
        for (ForceEmitter e : emitters) {
            e.tick(dt);
        }
        // Need to rebuild the cloud grid each tick, as clouds may have moved


    }
}

