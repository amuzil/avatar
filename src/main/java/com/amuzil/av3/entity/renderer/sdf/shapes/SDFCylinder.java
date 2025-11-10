package com.amuzil.av3.entity.renderer.sdf.shapes;

import com.amuzil.av3.entity.renderer.sdf.SignedDistanceFunction;
import com.amuzil.av3.entity.renderer.sdf.channels.floats.ConstantFloatChannel;
import com.amuzil.av3.entity.renderer.sdf.channels.floats.IFloatChannel;
import com.amuzil.av3.entity.renderer.sdf.transforms.AnimatedTransform;
import org.joml.Vector3f;

public class SDFCylinder implements SignedDistanceFunction {
    public final AnimatedTransform a = new AnimatedTransform();
    public IFloatChannel halfHeight = new ConstantFloatChannel(1.0f);
    public IFloatChannel radius = new ConstantFloatChannel(0.5f);

    private final Vector3f local = new Vector3f();

    @Override
    public float sd(Vector3f pWorld, float t) {
        a.update(t);
        a.xform.worldToLocal(pWorld, local);

        float h = halfHeight.eval(t);
        float r = radius.eval(t);

        Vector3f d = new Vector3f(
                (float)Math.sqrt(local.x * local.x + local.z * local.z) - r,
                Math.abs(local.y) - h,
                0f
        );

        float outside = Math.max(d.x, 0f);
        float inside  = Math.min(Math.max(d.x, d.y), 0f);
        float len     = (float)Math.sqrt(Math.max(d.x,0f)*Math.max(d.x,0f) + Math.max(d.y,0f)*Math.max(d.y,0f));
        return inside + len;
    }
}
