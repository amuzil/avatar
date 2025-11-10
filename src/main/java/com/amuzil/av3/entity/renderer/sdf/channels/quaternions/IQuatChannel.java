package com.amuzil.av3.entity.renderer.sdf.channels.quaternions;

import org.joml.Quaternionf;

public interface IQuatChannel {
    Quaternionf eval(float t, Quaternionf out);
}
