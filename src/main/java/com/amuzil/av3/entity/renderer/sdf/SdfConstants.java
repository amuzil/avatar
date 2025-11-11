package com.amuzil.av3.entity.renderer.sdf;

import com.amuzil.av3.entity.renderer.sdf.shapes.SDFBox;
import com.amuzil.av3.entity.renderer.sdf.shapes.SDFSphere;

public class SdfConstants {
    public static SignedDistanceFunction SPHERE;
    public static SignedDistanceFunction CUBE;
    public static void init() {
        SdfManager manager = SdfManager.INSTANCE;
        SPHERE = new SDFSphere();
        manager.registerSdf("sphere", SPHERE, false);
        CUBE = new SDFBox();
        manager.registerSdf("cube", CUBE, false);
    }
}
