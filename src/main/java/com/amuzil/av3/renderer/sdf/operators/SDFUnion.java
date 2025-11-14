package com.amuzil.av3.renderer.sdf.operators;

import com.amuzil.av3.renderer.sdf.SignedDistanceFunction;
import org.joml.Vector3f;

public class SDFUnion implements SignedDistanceFunction {
    private final SignedDistanceFunction a,b;
    public SDFUnion(SignedDistanceFunction a,SignedDistanceFunction b){this.a=a;this.b=b;}
    @Override public float sd(Vector3f p, float t){ return Math.min(a.sd(p, t), b.sd(p, t)); }
}
