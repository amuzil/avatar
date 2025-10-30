package com.amuzil.omegasource.entity.renderer.sdf.shapes;

import com.amuzil.omegasource.entity.renderer.sdf.SignedDistanceFunction;
import org.joml.Vector3f;

public class SDFSphere implements SignedDistanceFunction {
    public final Vector3f c; public final float r;
    public SDFSphere(Vector3f center, float radius){ this.c = new Vector3f(center); this.r = radius; }
    @Override public float sd(Vector3f p){ return p.distance(c) - r; }
    @Override public AABB aabb(){ return new AABB(c.x-r,c.y-r,c.z-r, c.x+r,c.y+r,c.z+r); }
}
