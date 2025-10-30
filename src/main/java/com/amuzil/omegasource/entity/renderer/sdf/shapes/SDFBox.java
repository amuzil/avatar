package com.amuzil.omegasource.entity.renderer.sdf.shapes;

import com.amuzil.omegasource.entity.renderer.sdf.SignedDistanceFunction;
import org.joml.Vector3f;

public class SDFBox implements SignedDistanceFunction {
    public final Vector3f c, h; // center, half extents
    public SDFBox(Vector3f center, Vector3f halfExtents){ this.c=new Vector3f(center); this.h=new Vector3f(halfExtents); }
    @Override public float sd(Vector3f p){
        float dx = Math.abs(p.x - c.x) - h.x;
        float dy = Math.abs(p.y - c.y) - h.y;
        float dz = Math.abs(p.z - c.z) - h.z;
        float ax = Math.max(dx, 0f), ay = Math.max(dy, 0f), az = Math.max(dz, 0f);
        float outside = (float)Math.sqrt(ax*ax + ay*ay + az*az);
        float inside  = Math.min(Math.max(dx, Math.max(dy, dz)), 0f);
        return outside + inside;
    }
    @Override public AABB aabb(){ return new AABB(c.x-h.x,c.y-h.y,c.z-h.z, c.x+h.x,c.y+h.y,c.z+h.z); }
}
