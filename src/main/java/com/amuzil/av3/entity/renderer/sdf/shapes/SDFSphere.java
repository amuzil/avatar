package com.amuzil.av3.entity.renderer.sdf.shapes;

import com.amuzil.av3.entity.renderer.sdf.SignedDistanceFunction;
import com.amuzil.av3.entity.renderer.sdf.channels.Channels;
import com.amuzil.av3.entity.renderer.sdf.channels.IFloatChannel;
import com.amuzil.av3.entity.renderer.sdf.transforms.AnimatedTransform;
import org.joml.Vector3f;

public class SDFSphere implements SignedDistanceFunction {
    public final AnimatedTransform a = new AnimatedTransform();
    public IFloatChannel radius = Channels.constant(1.0f);

    private final Vector3f local = new Vector3f();

    @Override
    public float sd(Vector3f pWorld, float t) {
        a.update(t); // refresh transform for this frame
        a.xform.worldToLocal(pWorld, local);
        float r = radius.eval(t);
        return local.length() - r;
    }
}
