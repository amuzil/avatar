package com.amuzil.av3.renderer.sdf.shapes;

import com.amuzil.av3.renderer.sdf.SignedDistanceFunction;
import com.amuzil.av3.renderer.sdf.channels.Channels;
import com.amuzil.av3.renderer.sdf.channels.IFloatChannel;
import com.amuzil.av3.renderer.sdf.transforms.AnimatedTransform;
import org.joml.Vector3f;

public class SDFTorus implements SignedDistanceFunction {
    public final AnimatedTransform a = new AnimatedTransform();
    public IFloatChannel majorRadius = Channels.constant(1.0f);
    public IFloatChannel minorRadius = Channels.constant(0.25f);

    // Scales torus thickness on Y by default.
    // 1.0 = normal torus
    // >1.0 = taller/thicker vertically
    // <1.0 = flatter vertically
    public IFloatChannel thickness = Channels.constant(1.0f);

    private final Vector3f local = new Vector3f();

    @Override
    public float sd(Vector3f pWorld, float t) {
        a.update(t);
        a.xform.worldToLocal(pWorld, local);

        float R = majorRadius.eval(t);
        float r = minorRadius.eval(t);

        float th = Math.max(0.0001f, thickness.eval(t));

        float qx = (float)Math.sqrt(local.x * local.x + local.z * local.z) - R;
        float qy = local.y / th;
        return (float)Math.sqrt(qx * qx + qy * qy) - r;
    }
}