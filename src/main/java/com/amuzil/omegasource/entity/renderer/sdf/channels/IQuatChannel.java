package com.amuzil.omegasource.entity.renderer.sdf.channels;

import org.joml.Quaternionf;

public interface IQuatChannel {
    Quaternionf eval(float t, Quaternionf out);
}
