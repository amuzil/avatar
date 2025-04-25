package com.amuzil.omegasource.utils.maths;

import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
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

    // ────────────────────────────────────────────────────────────────────────────────
    // 1) De Casteljau evaluation for an array of floats (used by both X and Y)
    // ────────────────────────────────────────────────────────────────────────────────
    private static float deCasteljau(float[] tmp, float t) {
        int n = tmp.length;
        for (int r = 1; r < n; r++) {
            for (int i = 0; i < n - r; i++) {
                tmp[i] = tmp[i] * (1 - t) + tmp[i + 1] * t;
            }
        }
        return tmp[0];
    }

    // ────────────────────────────────────────────────────────────────────────────────
    // 2) Binary‐search inversion: find u so that X(u) ≈ t
    // ────────────────────────────────────────────────────────────────────────────────
    private static float invertBezier(List<Float> xs, float t) {
        t = clamp(t);
        float low = 0f, high = 1f, mid = t;
        for (int i = 0; i < 10; i++) {
            mid = (low + high) * 0.5f;
            // prepare x-array for De Casteljau
            float[] tmp = new float[xs.size()];
            for (int j = 0; j < xs.size(); j++) tmp[j] = xs.get(j);
            float xAtMid = deCasteljau(tmp, mid);
            if (xAtMid < t) low = mid;
            else high = mid;
        }
        return mid;
    }

    // ────────────────────────────────────────────────────────────────────────────────
    // 3) General 2D Bézier: requires xs and ys lists of equal length
    // ────────────────────────────────────────────────────────────────────────────────
    public static float bezier(List<Float> xs, List<Float> ys, float t) {
        if (xs.size() != ys.size()) {
            throw new IllegalArgumentException("xs and ys must have same length");
        }
        // find parameter u so X(u)=t
        float u = invertBezier(xs, t);
        // evaluate Y(u)
        float[] tmp = new float[ys.size()];
        for (int i = 0; i < ys.size(); i++) tmp[i] = ys.get(i);
        return deCasteljau(tmp, u);
    }

    // ────────────────────────────────────────────────────────────────────────────────
    // 4) Overload for Point lists (2D): returns a Point on the curve
    // ────────────────────────────────────────────────────────────────────────────────
    public static Point bezierPoint(List<Point> cps, float t) {
        t = clamp(t);
        int n = cps.size();
        // copy into arrays
        float[] xs = new float[n], ys = new float[n];
        for (int i = 0; i < n; i++) {
            xs[i] = (float) cps.get(i).x();
            ys[i] = (float) cps.get(i).y();
        }
        // invert X→u
        float u = invertBezier(toFloatList(xs), t);
        // evaluate both
        return new Point(deCasteljau(xs.clone(), u),
                deCasteljau(ys.clone(), u));
    }

    // helper to convert primitive float[] to List<Float> for invertBezier
    private static List<Float> toFloatList(float[] arr) {
        List<Float> l = new ArrayList<>(arr.length);
        for (float v : arr) l.add(v);
        return l;
    }

    public static float evaluate(List<Point> points, float t) {
        List<Float> xs = new ArrayList<>(), ys = new ArrayList<>();
        for (Point p : points) {
            xs.add((float) p.x());
            ys.add((float) p.y());
        }
        return Easings.bezier(xs, ys, t);
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

    // ---- Examples of common Bezier presets ---- (see Constants class).

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

    /** Constants.
     *
     */

    // Important Mathematical Presets. Uses lists of Points. Pass these into a bezier curve to interpolate what you need.
    // Important mathematical presets: each list represents a cubic-bezier curve
    public static final List<Point> SIMPLE_EASE = List.of(
            new Point(0f, 0f),
            new Point(0.25f, 0.10f),
            new Point(0.25f, 1f),
            new Point(1f, 1f)
    );

    public static final List<Point> EASE_IN = List.of(
            new Point(0f, 0f),
            new Point(0.42f, 0f),
            new Point(1f, 1f),
            new Point(1f, 1f)
    );

    public static final List<Point> EASE_OUT = List.of(
            new Point(0f, 0f),
            new Point(0f, 0f),
            new Point(0.58f, 1f),
            new Point(1f, 1f)
    );

    public static final List<Point> EASE_IN_OUT = List.of(
            new Point(0f, 0f),
            new Point(0.42f, 0f),
            new Point(0.58f, 1f),
            new Point(1f, 1f)
    );

    public static final List<Point> EASE_IN_QUAD = List.of(
            new Point(0f, 0f),
            new Point(0.33f, 0f),
            new Point(0.67f, 0.33f),
            new Point(1f, 1f)
    );

    public static final List<Point> EASE_OUT_QUAD = List.of(
            new Point(0f, 0f),
            new Point(0.33f, 0.67f),
            new Point(0.67f, 1f),
            new Point(1f, 1f)
    );

    public static final List<Point> EASE_IN_CUBIC = List.of(
            new Point(0f, 0f),
            new Point(0.32f, 0f),
            new Point(0.67f, 0f),
            new Point(1f, 1f)
    );

    public static final List<Point> EASE_OUT_CUBIC = List.of(
            new Point(0f, 0f),
            new Point(0.33f, 1f),
            new Point(0.68f, 1f),
            new Point(1f, 1f)
    );

    public static final List<Point> EASE_IN_OUT_BACK = List.of(
            new Point(0f, 0f),
            new Point(0.68f, -0.6f),
            new Point(0.32f, 1.6f),
            new Point(1f, 1f)
    );

    // Elemental Presets

    public static final List<Point> FIRE_CURVE = List.of(
            new Point(0.00, 0.00),  // t=0: zero width
            new Point(0.20, 0.25),  // rise slowly
            new Point(0.40, 1.50),  // flare to 150%
            new Point(0.70, 0.40),  // rapid taper
            new Point(1.00, 0.00)   // die out completely
    );

    public static final List<Point> EARTH_CURVE = List.of(
            new Point(0.00, 0.00),  // t=0: at rest
            new Point(0.30, 1.00),  // lift steadily to full height
            new Point(0.60, 0.90),  // hold just below full
            new Point(0.85, 1.10),  // slight overshoot bounce
            new Point(1.00, 0.20)   // heavy squash to 20%
    );

    public static final List<Point> AIR_CURVE = List.of(
            new Point(0.00, 1.00),  // start at normal size
            new Point(0.25, 1.15),  // slight stretch
            new Point(0.50, 0.85),  // gentle contraction
            new Point(0.75, 1.15),  // stretch again
            new Point(1.00, 1.00)   // back to normal
    );

    public static final List<Point> WATER_CURVE = List.of(
            new Point(0.00, 0.00),  // in flight
            new Point(0.50, 1.00),  // at 50% life: impact → full width
            new Point(0.55, 1.50),  // quick rebound = 150%
            new Point(0.70, 0.90),  // settle slightly below full
            new Point(1.00, 1.00)   // calm at normal size
    );
}
