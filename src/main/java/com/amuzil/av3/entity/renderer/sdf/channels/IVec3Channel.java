package com.amuzil.av3.entity.renderer.sdf.channels;

import org.joml.Vector3f;

public interface IVec3Channel {
    Vector3f eval(float t, Vector3f out);
}
