package com.amuzil.omegasource.entity.renderer.sdf.operators;

import com.amuzil.omegasource.entity.renderer.sdf.SignedDistanceFunction;
import org.joml.Vector3f;

public class SDFIntersect implements SignedDistanceFunction {
    private final SignedDistanceFunction a,b;
    public SDFIntersect(SignedDistanceFunction a,SignedDistanceFunction b){this.a=a;this.b=b;}
    @Override public float sd(Vector3f p){ return Math.max(a.sd(p), b.sd(p)); }
}
