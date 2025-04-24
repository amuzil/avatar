package com.amuzil.omegasource.utils;

import net.minecraft.util.Mth;

import java.util.function.Function;

public class Easings {

    /**
     * Clamp t to [0,1] for safety.
     */
    private static float clamp(float t) {
        return Mth.clamp(t, 0f, 1f);
    }

    // ---- Linear ----

    /**
     * f(t) = t
     */
    public static float linear(float t) {
        return clamp(t);
    }

    // ---- Quadratic ----

    /**
     * f(t) = t²
     */
    public static float quadEaseIn(float t) {
        t = clamp(t);
        return t * t;
    }

    /**
     * f(t) = 1 − (1−t)²
     */
    public static float quadEaseOut(float t) {
        t = clamp(t);
        return 1 - (1 - t) * (1 - t);
    }

    /**
     * f(t) = if t<½ then 2t² else 1 − (−2t+2)²/2
     */
    public static float quadEaseInOut(float t) {
        t = clamp(t);
        return (t < 0.5f)
                ? 2 * t * t
                : (float) (1 - Math.pow(-2 * t + 2, 2) / 2);
    }

    // ---- Cubic ----

    /**
     * f(t) = t³
     */
    public static float cubicEaseIn(float t) {
        t = clamp(t);
        return t * t * t;
    }

    /**
     * f(t) = 1 − (1−t)³
     */
    public static float cubicEaseOut(float t) {
        t = clamp(t);
        float u = 1 - t;
        return 1 - u * u * u;
    }

    /**
     * f(t) = if t<½ then 4t³ else 1 − (−2t+2)³/2
     */
    public static float cubicEaseInOut(float t) {
        t = clamp(t);
        return (t < 0.5f)
                ? 4 * t * t * t
                : (float) (1 - Math.pow(-2 * t + 2, 3) / 2);
    }

    // ---- Quintic (“Smootherstep”) ----

    /**
     * f(t) = 6t⁵ − 15t⁴ + 10t³
     * Produces zero velocity & acceleration at endpoints.
     */
    public static float quinticEaseInOut(float t) {
        t = clamp(t);
        // optimized form: t^3 * (10 + t * (-15 + 6t))
        return t * t * t * (10f + t * (-15f + 6f * t));
    }

    // ---- Cubic-Bezier ----

    /**
     * Given control x1,x2 and target t, find u such that x(u)=t
     */
    private static float solveBezierU(float t, float x1, float x2) {
        // initial guess (assume linear)
        float u = t;
        for (int i = 0; i < 4; i++) {  // 4 iterations is plenty
            float xu = bezierCoord(u, x1, x2);
            float dx = bezierCoordDerivative(u, x1, x2);
            if (dx == 0f) break;
            u -= (xu - t) / dx;
            u = Mth.clamp(u, 0f, 1f);
        }
        return u;
    }

    /** Bezier X‐component at param u */
    private static float bezierCoord(float u, float c1, float c2) {
        float inv = 1 - u;
        return 3 * inv * inv * u * c1
                + 3 * inv * u * u * c2
                + u * u * u;
    }

    /** Derivative d/d u of Bezier X‐component */
    private static float bezierCoordDerivative(float u, float c1, float c2) {
        float inv = 1 - u;
        return 3 * (   (inv * inv) * c1
                + 2 * inv * u * (c2 - c1)
                + u * u * (1 - c2) );
    }

    /**
     * Generic cubic-Bezier easing. Assumes P0=(0,0), P3=(1,1).
     * You supply control points P1=(x1,y1), P2=(x2,y2).
     * <p>
     * Note: this simple version treats t as the parameter for both x and y;
     * for perfect timing, you'd invert the x(t) function, but for most VFX
     * this gives a good approximation.
     */
    public static float cubicBezier(float t, float x1, float y1, float x2, float y2) {
        t = Mth.clamp(t, 0f, 1f);
        // 1) Find curve parameter u so that BezierX(u) == t
        float u = solveBezierU(t, x1, x2);

        // 2) Compute BezierY(u)
        float inv = 1 - u;
        return inv*inv*inv * 0f
                + 3*inv*inv*u * y1
                + 3*inv*u*u * y2
                + u*u*u * 1f;
    }

    /**
     * Blends the “y” coordinates of a cubic Bézier curve at parameter t,
     * assuming P₀=(0,0) and P₃=(1,1).  Returns:
     * B₃,₀(t)*P₀.y + B₃,₁(t)*y1 + B₃,₂(t)*y2 + B₃,₃(t)*P₃.y
     * where Bₙ,ᵢ(t)=binomial(n,i)*tⁱ*(1−t)ⁿ⁻ⁱ.
     */
    private static float uuv(
            float u,   // (1−t)
            float t,   // parameter
            float uu,  // (1−t)²
            float tt,  // t²
            float ttt, // t³
            float y1,  // control point 1 y
            float y2   // control point 2 y
    ) {
        // B₃,₀ * 0 + B₃,₁ * y1 + B₃,₂ * y2 + B₃,₃ * 1
        return 3f * uu * t * y1   // B₃,₁ * y1
                + 3f * u * tt * y2   // B₃,₂ * y2
                + ttt;                // B₃,₃ * 1
    }

    // ---- Examples of common Bezier presets ----

    /**
     * CSS standard “ease” (0.25,0.1, 0.25,1.0)
     */
    public static float ease(float t) {
        return cubicBezier(t, 0.25f, 0.1f, 0.25f, 1f);
    }

    /**
     * CSS “ease-in-out” (0.42,0, 0.58,1)
     */
    public static float easeInOut(float t) {
        return cubicBezier(t, 0.42f, 0f, 0.58f, 1f);
    }

    // ---- Utility ----

    /**
     * Interpolates between start and end by easeFactor(t).
     * Example: float size = interpolate(startSize, maxSize,
     * Easing::quinticEaseInOut, t);
     */
    public static float interpolate(float start, float end,
                                    Function<Float, Float> easeFunc, float t) {
        return start + (end - start) * easeFunc.apply(t);
    }
}
