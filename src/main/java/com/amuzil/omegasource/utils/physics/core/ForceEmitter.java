package com.amuzil.omegasource.utils.physics.core;

import java.util.List;

public class ForceEmitter {

    private List<ForceCloud> clouds;

    public void tick() {
        clouds.forEach(ForceCloud::tick);
    }
}
