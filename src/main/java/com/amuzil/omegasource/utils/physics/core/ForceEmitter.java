package com.amuzil.omegasource.utils.physics.core;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ForceEmitter {

    private List<ForceCloud> clouds;
    private List<Vec3[][][]> fields;

    public ForceEmitter() {
        this.fields = new ArrayList<>();
    }

    public void tick() {
        clouds.forEach(ForceCloud::tick);

    }

    public List<Vec3[][][]> vectorFields() {
        return fields;
    }
}
