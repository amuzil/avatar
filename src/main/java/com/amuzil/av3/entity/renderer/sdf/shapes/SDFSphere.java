package com.amuzil.av3.entity.renderer.sdf.shapes;

import com.amuzil.av3.entity.renderer.sdf.SignedDistanceFunction;
import com.amuzil.av3.entity.renderer.sdf.channels.floats.ConstantFloatChannel;
import com.amuzil.av3.entity.renderer.sdf.channels.floats.IFloatChannel;
import com.amuzil.av3.entity.renderer.sdf.transforms.AnimatedTransform;
import org.joml.Vector3f;

public class SDFSphere implements SignedDistanceFunction {
    public final AnimatedTransform a = new AnimatedTransform();
    public IFloatChannel radius = new ConstantFloatChannel(1.0f);

    private transient final Vector3f local = new Vector3f();

    @Override
    public float sd(Vector3f pWorld, float t) {
        a.update(t); // refresh transform for this frame
        a.xform.worldToLocal(pWorld, local);
        float r = radius.eval(t);
        return local.length() - r;
    }
}
