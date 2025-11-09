package com.amuzil.av3.entity.renderer.sdf.shapes;

import com.amuzil.av3.entity.renderer.sdf.SignedDistanceFunction;
import com.amuzil.av3.entity.renderer.sdf.channels.Channels;
import com.amuzil.av3.entity.renderer.sdf.channels.IFloatChannel;
import com.amuzil.av3.entity.renderer.sdf.transforms.AnimatedTransform;
import org.joml.Vector3f;

public class SDFArcTorus implements SignedDistanceFunction {
    public final AnimatedTransform a = new AnimatedTransform();
    public IFloatChannel majorRadius = Channels.constant(1.0f);
    public IFloatChannel minorRadius = Channels.constant(0.25f);
    public IFloatChannel arcAngle = Channels.constant((float)Math.PI / 2f); // radians

    private final Vector3f local = new Vector3f();

    @Override
    public float sd(Vector3f pWorld, float t) {
        a.update(t);
        a.xform.worldToLocal(pWorld, local);

        float R = majorRadius.eval(t);
        float r = minorRadius.eval(t);
        float arc = arcAngle.eval(t);

        // Polar angle in XZ
        float angle = (float)Math.atan2(local.z, local.x);
        float halfArc = arc * 0.5f;
        float clamped = Math.max(-halfArc, Math.min(halfArc, angle));

        // Rotate to arc’s local frame
        float cosA = (float)Math.cos(clamped);
        float sinA = (float)Math.sin(clamped);
        float px = (float)Math.sqrt(local.x * local.x + local.z * local.z);
        float qx = px * cosA - R;
        float qy = local.y;
        return (float)Math.sqrt(qx * qx + qy * qy) - r;
    }
}
