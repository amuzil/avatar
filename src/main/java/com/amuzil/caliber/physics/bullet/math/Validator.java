package com.amuzil.caliber.physics.bullet.math;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class Validator {

    public static boolean finite(float f) {
        return Float.isFinite(f);
    }

    public static boolean finite(Vector3f v) {
        return Float.isFinite(v.x) && Float.isFinite(v.y) && Float.isFinite(v.z);
    }

    public static boolean finite(Quaternion q) {
        return Float.isFinite(q.getX()) && Float.isFinite(q.getY()) &&
                Float.isFinite(q.getZ()) && Float.isFinite(q.getW());
    }

    public static boolean nonZero(Vector3f v) {
        return v.lengthSquared() > 1e-12f; // avoid normalization of near-zero vectors
    }
}
