package com.amuzil.omegasource.entity.renderer.sdf.operators;

import com.amuzil.omegasource.entity.renderer.sdf.SignedDistanceFunction;
import org.joml.Vector3f;

public class SDFSmoothUnion implements SignedDistanceFunction {
    private final SignedDistanceFunction a,b; private final float k;
    public SDFSmoothUnion(SignedDistanceFunction a,SignedDistanceFunction b,float k){this.a=a;this.b=b;this.k=k;}
    @Override public float sd(Vector3f p){
        float da = a.sd(p), db = b.sd(p);
        float h = Math.max(0f, Math.min(1f, 0.5f + 0.5f*(db - da)/k));
        return lerp(db, da, h) - k*h*(1f - h);
    }
    private static float lerp(float a,float b,float t){ return a + (b-a)*t; }
}
