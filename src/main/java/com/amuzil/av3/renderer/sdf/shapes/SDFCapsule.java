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

        // radial distance in XZ plane
        float qx = (float) Math.sqrt(local.x * local.x + local.z * local.z);
        // axial distance past the "cylinder" core
        float qy = Math.abs(local.y) - h;

        float ox = Math.max(qx, 0f);
        float oy = Math.max(qy, 0f);

        float outside = (float) Math.sqrt(ox * ox + oy * oy);
        float inside = Math.min(Math.max(qx, qy), 0f);

        return outside + inside - r;
    }
}
