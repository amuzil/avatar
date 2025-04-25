package com.amuzil.omegasource.utils;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.utils.maths.Point;

import java.util.LinkedList;
import java.util.List;


public final class Constants {
    // This class is for storing constant values and strings

    public static final String MOD_ID = Avatar.MOD_ID;

    private Constants() {
        // Private constructor to prevent instantiation
    }


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

}
