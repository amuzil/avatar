package com.amuzil.av3.renderer.sdf.shapes;

import com.amuzil.av3.renderer.sdf.SignedDistanceFunction;
import com.amuzil.av3.renderer.sdf.channels.Channels;
import com.amuzil.av3.renderer.sdf.channels.IFloatChannel;
import com.amuzil.av3.renderer.sdf.transforms.AnimatedTransform;
import org.joml.Vector3f;

public class SDFDisk implements SignedDistanceFunction {
    public final AnimatedTransform a = new AnimatedTransform();
    public IFloatChannel radius = Channels.constant(1.0f);
    public IFloatChannel thickness = Channels.constant(0.01f); // vertical half-thickness

    private final Vector3f local = new Vector3f();

    @Override
    public float sd(Vector3f pWorld, float t) {
        a.update(t);
        a.xform.worldToLocal(pWorld, local);

        float r = radius.eval(t);
        float h = thickness.eval(t);

        float d = (float)Math.sqrt(local.x * local.x + local.z * local.z) - r;
        float y = Math.abs(local.y) - h;
        float outside = (float)Math.sqrt(Math.max(d,0f)*Math.max(d,0f) + Math.max(y,0f)*Math.max(y,0f));
        float inside  = Math.min(Math.max(d, y), 0f);
        return outside + inside;
    }
}
