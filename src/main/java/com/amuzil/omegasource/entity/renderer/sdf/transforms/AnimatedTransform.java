package com.amuzil.omegasource.entity.renderer.sdf.transforms;

import com.amuzil.omegasource.entity.renderer.sdf.channels.IQuatChannel;
import com.amuzil.omegasource.entity.renderer.sdf.channels.IVec3Channel;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class AnimatedTransform {
    public Transform xform = new Transform();

    public IVec3Channel pos = (t,out)->out.set(0,0,0);
    public IQuatChannel rot = (t, out)->out.identity();
    public IVec3Channel scl = (t, out)->out.set(1,1,1);

    private final Vector3f tmpV = new Vector3f();
    private final Quaternionf tmpQ = new Quaternionf();

    public void update(float t) {
        pos.eval(t, xform.position);
        rot.eval(t, xform.rotation);
        scl.eval(t, xform.scale);
        xform.computeMatrices();
    }
}
