package com.amuzil.av3.entity.renderer.sdf.channels.vectors;

import org.joml.Vector3f;

public class ConstantVectorChannel implements IVec3Channel {
    public final Vector3f value;

    public ConstantVectorChannel(Vector3f value) {
        this.value = value;
    }

    @Override
    public Vector3f eval(float t, Vector3f out) {
        return value;
    }
}
