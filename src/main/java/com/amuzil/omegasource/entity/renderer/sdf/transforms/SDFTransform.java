package com.amuzil.omegasource.entity.renderer.sdf.transforms;

import com.amuzil.omegasource.entity.renderer.sdf.SignedDistanceFunction;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class SDFTransform implements SignedDistanceFunction {
    private final SignedDistanceFunction inner;
    private final Matrix3f rot;   // rotation only (keep uniform scale 1 for MC)
    private final Vector3f pos;   // translation
    public SDFTransform(SignedDistanceFunction inner, Matrix3f rot, Vector3f pos){ this.inner=inner; this.rot=new Matrix3f(rot); this.pos=new Vector3f(pos); }
    @Override public float sd(Vector3f p, float t){
        Vector3f q = new Vector3f(p).sub(pos); // inverse translate
        rot.transpose();                       // inverse rotate (for pure rotation)
        q.mul(rot);
        rot.transpose();                       // restore
        return inner.sd(q, t);
    }
    @Override public AABB aabb(){ return AABB.INF; } // conservative; or transform corners of inner aabb
}
