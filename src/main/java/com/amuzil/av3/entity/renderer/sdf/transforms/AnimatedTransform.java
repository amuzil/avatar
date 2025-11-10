package com.amuzil.av3.entity.renderer.sdf.transforms;

import com.amuzil.av3.entity.renderer.sdf.channels.quaternions.ConstantQuaternionChannel;
import com.amuzil.av3.entity.renderer.sdf.channels.quaternions.IQuatChannel;
import com.amuzil.av3.entity.renderer.sdf.channels.vectors.ConstantVectorChannel;
import com.amuzil.av3.entity.renderer.sdf.channels.vectors.IVec3Channel;
import org.joml.Vector3f;

public class AnimatedTransform {
    public Transform xform = new Transform();

    public IVec3Channel pos = new ConstantVectorChannel(new Vector3f(0,0,0));
    public IQuatChannel rot = new ConstantQuaternionChannel();
    public IVec3Channel scl = new ConstantVectorChannel(new Vector3f(1, 1, 1));

    public void update(float t) {
        pos.eval(t, xform.position);
        rot.eval(t, xform.rotation);
        scl.eval(t, xform.scale);
        xform.computeMatrices();
    }
}
