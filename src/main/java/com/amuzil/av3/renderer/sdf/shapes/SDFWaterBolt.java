package com.amuzil.av3.renderer.sdf.shapes;

import com.amuzil.av3.renderer.sdf.SignedDistanceFunction;
import com.amuzil.av3.renderer.sdf.channels.Channels;
import com.amuzil.av3.renderer.sdf.channels.IFloatChannel;
import com.amuzil.av3.renderer.sdf.transforms.AnimatedTransform;
import org.joml.Vector3f;

public class SDFWaterBolt implements SignedDistanceFunction {
    public final AnimatedTransform a = new AnimatedTransform();
    private final Vector3f local = new Vector3f();
    // Tail length from head base (y=0) to tail end (y=-Length)
    public IFloatChannel length = Channels.constant(4.0f);
    // Radii
    public IFloatChannel headRadius = Channels.constant(0.55f);
    public IFloatChannel tailRadius = Channels.constant(0.08f);
    // Taper curve: >1 keeps it thicker longer then thins rapidly near the tail
    public IFloatChannel taperExponent = Channels.constant(1.7f);
    // Head styling
    // 0 = rounder blob, 1 = more spear-ish nose
    public IFloatChannel headTipBlend = Channels.constant(0.55f);
    // How far the nose extends forward (+Y)
    public IFloatChannel headTipLength = Channels.constant(0.9f);
    // At headTipBlend=1, tip radius becomes headRadius * headTipRadiusMul
    // (keep this >0 to avoid needle tips)
    public IFloatChannel headTipRadiusMul = Channels.constant(0.35f);
    // Smooth union strength in world units (scaled by head radius inside)
    public IFloatChannel smoothK = Channels.constant(0.25f);

    // Tapered capsule / rounded cone along Y between ya -> yb, with radius ra -> rb.
    // Uses distance to closest point on segment, minus radius at that parameter.
    private static float sdTaperedSegmentAxisY(Vector3f p, float ya, float yb, float ra, float rb, float exponent) {
        float baY = (yb - ya);
        float invLen2 = 1.0f / (baY * baY + 1e-12f);

        // projection parameter (works even if baY is negative)
        float h = clamp01((p.y - ya) * baY * invLen2);

        float hh = (exponent == 1.0f) ? h : (float) Math.pow(h, exponent);
        float r = lerp(ra, rb, hh);

        float cy = ya + baY * h;

        float dx = p.x;
        float dy = p.y - cy;
        float dz = p.z;

        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz) - r;
    }

    // ===== helpers =====

    private static float sdSphere(float x, float y, float z, float r) {
        return (float) Math.sqrt(x * x + y * y + z * z) - r;
    }

    // Polynomial smooth-min (IQ style). k=0 -> hard min.
    private static float smin(float a, float b, float k) {
        if (k <= 0.0f) return Math.min(a, b);
        float h = clamp01(0.5f + 0.5f * (b - a) / k);
        return lerp(b, a, h) - k * h * (1.0f - h);
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static float clamp01(float v) {
        return v < 0.0f ? 0.0f : (v > 1.0f ? 1.0f : v);
    }

    @Override
    public float sd(Vector3f pWorld, float t) {
        a.update(t);
        a.xform.worldToLocal(pWorld, local);

        float L = Math.max(1e-6f, length.eval(t));
        float rHead = Math.max(1e-6f, headRadius.eval(t));
        float rTail = Math.max(1e-6f, tailRadius.eval(t));
        float exp = Math.max(1e-3f, taperExponent.eval(t));

        float tipBlend = clamp01(headTipBlend.eval(t));
        float tipLen = Math.max(0.0f, headTipLength.eval(t));
        float tipMul = Math.max(0.05f, headTipRadiusMul.eval(t)); // prevent absurd needle
        float k = Math.max(0.0f, smoothK.eval(t)) * rHead;

        // Body: tapered capsule along Y from head base (0) to tail end (-L)
        float dBody = sdTaperedSegmentAxisY(local, 0.0f, -L, rHead, rTail, exp);

        // Nose: short tapered segment forward (+Y), radius transitions from rTip -> rHead
        // rTip interpolates between blob-ish and spear-ish
        float rTip = lerp(rHead, rHead * tipMul, tipBlend);
        float dNose = sdTaperedSegmentAxisY(local, tipLen, 0.0f, rTip, rHead, 1.0f);

        // Optional: slight head bulge (keeps it from looking too "cone-y")
        // This is subtle and helps land between blob and spear tip.
        float dBulge = sdSphere(local.x, local.y - (0.10f * tipLen), local.z, rHead * 1.05f);

        // Combine: body + nose + bulge
        float d = smin(dBody, dNose, k);
        d = smin(d, dBulge, 0.6f * k);

        return d;
    }
}
