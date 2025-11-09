package com.amuzil.magus.physics.core;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ForceEmitter {

    private List<ForceCloud> clouds;
    private final List<Vec3[][][]> fields;

    public ForceEmitter() {
        this.fields = new ArrayList<>();
    }

    public void tick(double dt) {
//        fields.clear();                       // clear last-frame fields
//        for (ForceCloud c : clouds) {
//            // 1) spawn new points via emitter’s spawn-modules
////            c.modules().stream()
////                    .filter(m -> m instanceof SpawnModule)
////                    .forEach(m -> ((SpawnModule)m).spawn(this, c, dt));
//
//            // 2) update cloud (preSolve/solve/postSolve)
//            c.tick();
//
//            // 3) sample into a vector field for use or rendering
//            Vec3[][][] field = c.buildVectorField(
//                    /*center=*/ c.pos(),
//                    /*nx=*/16, /*ny=*/16, /*nz=*/16,
//                    /*cellDim=*/0.5
//            );
//            fields.add(field);
//
//            // Now combine relevant fields from other clouds in the system, and solve interactions
//            // (This will be done at the ForceSystem level eventually)
//        }

    }


    public List<ForceCloud> getClouds() {
        return clouds;
    }

    public List<Vec3[][][]> vectorFields() {
        return fields;
    }

    public void addCloud(ForceCloud c) {
        clouds.add(c);
    }

    public void removeCloud(ForceCloud c) {
        clouds.remove(c);
    }

}
