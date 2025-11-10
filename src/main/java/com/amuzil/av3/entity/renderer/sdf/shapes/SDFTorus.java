package com.amuzil.av3.entity.renderer.sdf.shapes;

import com.amuzil.av3.entity.renderer.sdf.SignedDistanceFunction;
import com.amuzil.av3.entity.renderer.sdf.channels.floats.ConstantFloatChannel;
import com.amuzil.av3.entity.renderer.sdf.channels.floats.IFloatChannel;
import com.amuzil.av3.entity.renderer.sdf.transforms.AnimatedTransform;
import org.joml.Vector3f;

public class SDFTorus implements SignedDistanceFunction {
    public final AnimatedTransform a = new AnimatedTransform();
    public IFloatChannel majorRadius = new ConstantFloatChannel(1.0f);
    public IFloatChannel minorRadius = new ConstantFloatChannel(0.25f);

    private final Vector3f local = new Vector3f();

    @Override
    public float sd(Vector3f pWorld, float t) {
        a.update(t);
        a.xform.worldToLocal(pWorld, local);

        float R = majorRadius.eval(t);
        float r = minorRadius.eval(t);

        float qx = (float)Math.sqrt(local.x * local.x + local.z * local.z) - R;
        float qy = local.y;
        return (float)Math.sqrt(qx * qx + qy * qy) - r;
    }
}