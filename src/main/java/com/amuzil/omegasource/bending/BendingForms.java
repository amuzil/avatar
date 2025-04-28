package com.amuzil.omegasource.bending;

public class BendingForms {
    // Basic Bending Forms

    public static final BendingForm NULL = new BendingForm("null"); // Blank Form
    public static final BendingForm STRIKE = new BendingForm("strike", BendingForm.Type.MOTION);
    public static final BendingForm BLOCK = new BendingForm("block", BendingForm.Type.MOTION);
    public static final BendingForm STEP = new BendingForm("step", BendingForm.Type.MOTION);
    public static final BendingForm PUSH = new BendingForm("push", BendingForm.Type.MOTION);
    public static final BendingForm PULL = new BendingForm("pull", BendingForm.Type.MOTION);
    public static final BendingForm LEFT = new BendingForm("left", BendingForm.Type.MOTION);
    public static final BendingForm RIGHT = new BendingForm("right", BendingForm.Type.MOTION);
    public static final BendingForm RAISE = new BendingForm("raise", BendingForm.Type.MOTION);
    public static final BendingForm LOWER = new BendingForm("lower", BendingForm.Type.MOTION);
    public static final BendingForm ROTATE = new BendingForm("rotate", BendingForm.Type.MOTION);
    public static final BendingForm EXPAND = new BendingForm("expand", BendingForm.Type.SHAPE);
    public static final BendingForm COMPRESS = new BendingForm("compress", BendingForm.Type.SHAPE);
    public static final BendingForm SPLIT = new BendingForm("split", BendingForm.Type.SHAPE);
    public static final BendingForm COMBINE = new BendingForm("combine", BendingForm.Type.SHAPE);
    public static final BendingForm ARC = new BendingForm("arc", BendingForm.Type.INITIALIZER); // Trigger & charge motion Forms
    public static final BendingForm SHAPE = new BendingForm("shape", BendingForm.Type.INITIALIZER); // Trigger & charge shape Forms
    public static final BendingForm FOCUS = new BendingForm("focus", BendingForm.Type.INITIALIZER);

    public static void init() {}
}
