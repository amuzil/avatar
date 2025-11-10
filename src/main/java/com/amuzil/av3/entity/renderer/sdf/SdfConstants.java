package com.amuzil.av3.entity.renderer.sdf;

import com.amuzil.av3.entity.renderer.sdf.shapes.SDFSphere;

public class SdfConstants {
    public static SignedDistanceFunction STATIC_SPHERE;
    public static void init() {
        STATIC_SPHERE = new SDFSphere();
    }
}
