package com.amuzil.av3.entity.renderer.sdf.channels.vectors;

import org.joml.Vector3f;

public class OrbitXZChannel implements IVec3Channel {
    public Vector3f center;
    public float radius;
    public float freq;

    public OrbitXZChannel(Vector3f center, float radius, float freq) {
        this.center = center;
        this.radius = radius;
        this.freq = freq;
    }

    @Override
    public Vector3f eval(float t, Vector3f out) {
        float a = (float)(2*Math.PI*freq*t);
        return out.set(center.x + radius*(float)Math.cos(a), center.y, center.z + radius*(float)Math.sin(a));
    }
}
