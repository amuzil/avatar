package com.amuzil.av3.renderer.sdf.channels;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class Channels {

    public static IVec3Channel add(IVec3Channel pos, IVec3Channel pos2) {
        Vector3f a = new Vector3f();
        Vector3f b = new Vector3f();
        return (t, out) -> {
            pos.eval(t, a);
            pos2.eval(t, b);
            return out.set(a).add(b); // out = a + b
        };
    }
    public static IFloatChannel constant(float v) {
        return t -> v;
    }

    public static IVec3Channel constantVec3(float x, float y, float z) {
        return (t, out) -> out.set(x, y, z);
    }

    public static IQuatChannel constantQuat(Quaternionf q) {
        Quaternionf qq = new Quaternionf(q);
        return (t, out) -> out.set(qq);
    }

    // -------- Entity sampling --------

    /**
     * World-space look direction, normalized.
     */
    public static IVec3Channel entityLookDir(Entity e, Supplier<Float> partialTicks) {
        return (t, out) -> {
            // 1.21-ish: getViewVector(partialTicks) exists on Entity in modern MC
            var v = e.getViewVector(partialTicks.get());
            return out.set((float) v.x, (float) v.y, (float) v.z).normalize();
        };
    }

    /**
     * World-space position (optionally interpolated).
     */
    public static IVec3Channel entityPos(Entity e, Supplier<Float> partialTicks) {
        return (t, out) -> {
            float pt = partialTicks.get();
            double x = e.xOld + (e.getX() - e.xOld) * pt;
            double y = e.yOld + (e.getY() - e.yOld) * pt;
            double z = e.zOld + (e.getZ() - e.zOld) * pt;
            return out.set((float) x, (float) y, (float) z);
        };
    }

    // -------- Rotation building --------

    /**
     * Returns a quaternion that rotates `localAxis` onto the (normalized) `dirWS`.
     * This is the "capsule faces look direction" channel.
     */
    public static IQuatChannel alignAxisToDir(Vector3f localAxis, IVec3Channel dirWS) {
        Vector3f axis = new Vector3f(localAxis).normalize();
        Vector3f d = new Vector3f();

        return (t, out) -> {
            dirWS.eval(t, d);
            if (d.lengthSquared() < 1e-8f) return out.identity();
            d.normalize();

            // JOML: rotationTo(a,b) rotates vector a onto vector b
            return out.identity().rotationTo(axis, d);
        };
    }

    /**
     * Optional: add roll around the final facing axis (good for “twisting water”).
     * Degrees per second is easiest to think in; change to rpm if you prefer.
     */
    public static IQuatChannel rollAroundLook(IVec3Channel dirWS, float degPerSec) {
        Vector3f d = new Vector3f();
        Quaternionf qRoll = new Quaternionf();

        return (t, out) -> {
            dirWS.eval(t, d);
            if (d.lengthSquared() < 1e-8f) return out.identity();
            d.normalize();

            float rad = (float) Math.toRadians(degPerSec) * t;
            return out.identity().fromAxisAngleRad(d.x, d.y, d.z, rad);
        };
    }

    /**
     * Compose rotations: result = a(t) * b(t)
     * (Order matters: this applies b after a in the usual JOML convention.)
     */
    public static IQuatChannel mul(IQuatChannel a, IQuatChannel b) {
        Quaternionf qa = new Quaternionf();
        Quaternionf qb = new Quaternionf();
        return (t, out) -> {
            a.eval(t, qa);
            b.eval(t, qb);
            return out.set(qa).mul(qb);
        };
    }

    // -------- Existing stuff you had --------

    public static IFloatChannel pulse(float base, float amp, float freq, float phase) {
        return t -> base + amp * 0.5f * (1f + (float) Math.sin((Math.PI * 2) * freq * t + phase));
    }

    public static IVec3Channel orbitXZ(Vector3f center, float radius, float freq) {
        return (t, out) -> {
            float a = (float) (2 * Math.PI * freq * t);
            return out.set(center.x + radius * (float) Math.cos(a), center.y, center.z + radius * (float) Math.sin(a));
        };
    }

    public static IQuatChannel spinY(float rpm) {
        return (t, out) -> {
            float deg = (t * rpm * 360f) % 360f;
            return out.rotationY((float) Math.toRadians(deg));
        };
    }

}
