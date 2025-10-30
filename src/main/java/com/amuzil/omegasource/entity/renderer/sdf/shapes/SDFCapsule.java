package com.amuzil.omegasource.entity.renderer.sdf.shapes;

import com.amuzil.omegasource.entity.renderer.sdf.SignedDistanceFunction;
import org.joml.Vector3f;

public class SDFCapsule implements SignedDistanceFunction {
    public final Vector3f a,b; public final float r;
    public SDFCapsule(Vector3f a, Vector3f b, float r){ this.a=new Vector3f(a); this.b=new Vector3f(b); this.r=r; }
    @Override public float sd(Vector3f p){
        Vector3f pa = new Vector3f(p).sub(a);
        Vector3f ba = new Vector3f(b).sub(a);
        float h = Math.max(0f, Math.min(1f, pa.dot(ba) / ba.dot(ba)));
        return new Vector3f(pa).sub(new Vector3f(ba).mul(h)).length() - r;
    }
    @Override public AABB aabb(){
        float minX=Math.min(a.x,b.x)-r, minY=Math.min(a.y,b.y)-r, minZ=Math.min(a.z,b.z)-r;
        float maxX=Math.max(a.x,b.x)+r, maxY=Math.max(a.y,b.y)+r, maxZ=Math.max(a.z,b.z)+r;
        return new AABB(minX,minY,minZ,maxX,maxY,maxZ);
    }
}
