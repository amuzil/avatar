package com.amuzil.av3.entity.renderer.sdf.shapes;

import com.amuzil.av3.entity.renderer.sdf.SignedDistanceFunction;
import com.amuzil.av3.entity.renderer.sdf.channels.floats.ConstantFloatChannel;
import com.amuzil.av3.entity.renderer.sdf.channels.floats.IFloatChannel;
import com.amuzil.av3.entity.renderer.sdf.transforms.AnimatedTransform;
import org.joml.Vector3f;

public class SDFTruncatedCone implements SignedDistanceFunction {
    public final AnimatedTransform a = new AnimatedTransform();
    public IFloatChannel halfHeight = new ConstantFloatChannel(1.0f);
    public IFloatChannel radiusTop = new ConstantFloatChannel(0.3f);
    public IFloatChannel radiusBottom = new ConstantFloatChannel(0.6f);

    private final Vector3f local = new Vector3f();

    @Override
    public float sd(Vector3f pWorld, float t) {
        a.update(t);
        a.xform.worldToLocal(pWorld, local);

        float h = halfHeight.eval(t);
        float ra = radiusTop.eval(t);
        float rb = radiusBottom.eval(t);

        // Adapted from Inigo Quilez’s “sdConeSection”
        Vector3f p = new Vector3f(local.x, local.y, local.z);
        float r = (float)Math.sqrt(p.x * p.x + p.z * p.z);
        float k1 = rb - ra;
        float k2 = 2f * h;
        float c = (r * k2 - p.y * k1 - k1 * h) / (k1 * k1 + k2 * k2);
        c = Math.max(0f, Math.min(1f, c));
        float qx = r - (ra + k1 * c);
        float qy = p.y - (-h + k2 * c);
        return (float)Math.sqrt(qx * qx + qy * qy);
    }
}