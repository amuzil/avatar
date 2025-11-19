package com.amuzil.av3.renderer.sdf.shapes;

import com.amuzil.av3.renderer.sdf.SignedDistanceFunction;
import com.amuzil.av3.renderer.sdf.channels.Channels;
import com.amuzil.av3.renderer.sdf.channels.IFloatChannel;
import com.amuzil.av3.renderer.sdf.transforms.AnimatedTransform;
import org.joml.Vector3f;

public class SDFCapsule implements SignedDistanceFunction {
    public final AnimatedTransform a = new AnimatedTransform();
    public IFloatChannel halfHeight = Channels.constant(1.0f);
    public IFloatChannel radius = Channels.constant(0.3f);

    private final Vector3f q = new Vector3f();
    private final Vector3f local = new Vector3f();

    @Override
    public float sd(Vector3f pWorld, float t) {
        a.update(t);
        a.xform.worldToLocal(pWorld, local);

        float h = halfHeight.eval(t);
        float r = radius.eval(t);

        // Capsule aligned to Y axis: two sphere caps joined by a cylinder
        // Adapted from Inigo Quilez's SDF formulation
        q.set(local.x, Math.abs(local.y) - h, local.z);

        // Outside part: distance from surface when beyond the cylindrical core
        float dx = Math.max(q.x, 0f);
        float dy = Math.max(q.y, 0f);
        float dz = Math.max(q.z, 0f);
        float outside = (float)Math.sqrt(dx * dx + dy * dy + dz * dz);

        // Inside part: signed distance inside the capsule volume
        float inside = Math.min(Math.max(q.x, Math.max(q.y, q.z)), 0f);

        return outside + inside - r;
    }
}
