package com.amuzil.av3.entity.renderer.sdf.channels.quaternions;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SpinYChannel implements IQuatChannel {
    public float rpm;

    public SpinYChannel(float rpm) {
        this.rpm = rpm;
    }

    @Override
    public Quaternionf eval(float t, Quaternionf out) {
        float deg = (t * rpm * 360f) % 360f;
        return out.rotationY((float)Math.toRadians(deg));
    }
}
