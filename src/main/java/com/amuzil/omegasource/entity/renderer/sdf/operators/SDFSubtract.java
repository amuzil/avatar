package com.amuzil.omegasource.entity.renderer.sdf.operators;

import com.amuzil.omegasource.entity.renderer.sdf.SignedDistanceFunction;
import org.joml.Vector3f;

public class SDFSubtract implements SignedDistanceFunction {
    private final SignedDistanceFunction a,b; // a \ b
    public SDFSubtract(SignedDistanceFunction a,SignedDistanceFunction b){this.a=a;this.b=b;}
    @Override public float sd(Vector3f p, float t){ return Math.max(a.sd(p, t), -b.sd(p, t)); }
}
