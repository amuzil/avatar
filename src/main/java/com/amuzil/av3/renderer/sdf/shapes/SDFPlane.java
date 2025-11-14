package com.amuzil.av3.renderer.sdf.shapes;

import com.amuzil.av3.renderer.sdf.SignedDistanceFunction;
import com.amuzil.av3.renderer.sdf.channels.Channels;
import com.amuzil.av3.renderer.sdf.channels.IFloatChannel;
import com.amuzil.av3.renderer.sdf.transforms.AnimatedTransform;
import org.joml.Vector3f;

public class SDFPlane implements SignedDistanceFunction {
    public final AnimatedTransform a = new AnimatedTransform();
    public final Vector3f normal = new Vector3f(0, 1, 0);
    public IFloatChannel height = Channels.constant(0f); // distance from origin along normal

    private final Vector3f local = new Vector3f();

    @Override
    public float sd(Vector3f pWorld, float t) {
        a.update(t);
        a.xform.worldToLocal(pWorld, local);
        float h = height.eval(t);
        return local.dot(normal) + h;
    }
}