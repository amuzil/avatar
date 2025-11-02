package com.amuzil.av3.entity.renderer.sdf.channels;

import org.joml.Vector3f;

public class Channels {
    public static IFloatChannel constant(float v) { return t -> v; }

    // Smooth pulse: base + amp * 0.5*(1+sin(2π f t + phase))
    public static IFloatChannel pulse(float base, float amp, float freq, float phase) {
        return t -> base + amp * 0.5f * (1f + (float)Math.sin((Math.PI*2)*freq*t + phase));
    }

    // Circular orbit in XZ plane
    public static IVec3Channel orbitXZ(Vector3f center, float radius, float freq) {
        return (t, out) -> {
            float a = (float)(2*Math.PI*freq*t);
            return out.set(center.x + radius*(float)Math.cos(a), center.y, center.z + radius*(float)Math.sin(a));
        };
    }

    // Y-up spin
    public static IQuatChannel spinY(float rpm) {
        return (t, out) -> {
            float deg = (t * rpm * 360f) % 360f;
            return out.rotationY((float)Math.toRadians(deg));
        };
    }
}
