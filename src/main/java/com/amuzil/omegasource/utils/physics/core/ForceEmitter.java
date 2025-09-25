package com.amuzil.omegasource.utils.physics.core;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ForceEmitter {

    private List<ForceCloud> clouds;
    private List<Vec3[][][]> fields;

    public ForceEmitter() {
        this.fields = new ArrayList<>();
        // ensure clouds list is initialized to avoid NPEs when adding/ticking
        this.clouds = new ArrayList<>();
    }

    public void tick(double dt) {
        fields.clear();                       // clear last-frame fields
        if (clouds == null || clouds.isEmpty()) return;

        // iterate over a copy so modules can add/remove clouds safely during tick
        List<ForceCloud> snapshot = new ArrayList<>(clouds);
        for (ForceCloud c : snapshot) {
            if (c == null) continue;

            // 1) spawn new points via emitter’s spawn-modules
//            c.modules().stream()
//                    .filter(m -> m instanceof SpawnModule)
//                    .forEach(m -> ((SpawnModule)m).spawn(this, c, dt));

            // 2) update cloud (preSolve/solve/postSolve)
            c.tick(dt);

            // 3) sample into a vector field for use or rendering (guarded)
            try {
                Vec3[][][] field = c.buildVectorField(
                        /*center=*/ c.pos(),
                        /*nx=*/16, /*ny=*/16, /*nz=*/16,
                        /*cellDim=*/0.5
                );
                fields.add(field);
            } catch (UnsupportedOperationException | NullPointerException ignored) {
                // if the cloud doesn't implement buildVectorField yet, skip gracefully
            }
        }

    }

    public List<Vec3[][][]> vectorFields() {
        return fields;
    }

    public void addCloud(ForceCloud c) {
        if (clouds == null) clouds = new ArrayList<>();
        if (c != null && !clouds.contains(c)) {
            // mark cloud with this emitter's id so the system can distinguish owner vs opposing emitters
            try {
                c.id(System.identityHashCode(this));
            } catch (Exception ignored) {
                // if cloud doesn't expose id setter for some reason, continue without owner tag
            }
            clouds.add(c);
        }
    }

    public void removeCloud(ForceCloud c) {
        if (clouds == null) return;
        // Optionally clear owner tag when removed
        try {
            if (c != null) c.id(-1);
        } catch (Exception ignored) { }
        clouds.remove(c);
    }

    // --- Added: safe accessor for system-level iteration ---
    /**
     * Returns the mutable list of clouds owned by this emitter.
     * Never returns null (returns empty list if no clouds).
     */
    public List<ForceCloud> getClouds() {
        if (clouds == null) clouds = new ArrayList<>();
        return clouds;
    }

}
