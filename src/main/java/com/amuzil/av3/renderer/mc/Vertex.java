package com.amuzil.av3.renderer.mc;

import org.joml.Vector3f;

public final class Vertex {
    public Vector3f position;
    public Vector3f normal;

    public Vertex(Vector3f position, Vector3f normal) {
        this.position = position;
        this.normal = normal;
    }


    static final float KEY_SCALE = 4096.0f;

    static final class VKey {
        final int x, y, z;
        VKey(Vector3f p) {
            this.x = Math.round(p.x * KEY_SCALE);
            this.y = Math.round(p.y * KEY_SCALE);
            this.z = Math.round(p.z * KEY_SCALE);
        }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof VKey k)) return false;
            return x == k.x && y == k.y && z == k.z;
        }
        @Override public int hashCode() {
            int h = x;
            h = 31 * h + y;
            h = 31 * h + z;
            return h;
        }
    }
}
