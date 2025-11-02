package com.amuzil.av3.bending.form;

import com.amuzil.av3.api.magus.registry.Registries;

import static com.amuzil.av3.bending.form.BendingForm.Type;


public class BendingForms {
    // Basic Bending Forms

    public static final BendingForm NULL = new BendingForm("null"); // Blank Form
    public static final BendingForm STRIKE = new BendingForm("strike", Type.MOTION);
    public static final BendingForm BLOCK = new BendingForm("block", Type.MOTION);
    public static final BendingForm STEP = new BendingForm("step", Type.MOTION);
    public static final BendingForm PUSH = new BendingForm("push", Type.MOTION, Type.Motion.FORWARD);
    public static final BendingForm PULL = new BendingForm("pull", Type.MOTION, Type.Motion.BACKWARD);
    public static final BendingForm LEFT = new BendingForm("left", Type.MOTION, Type.Motion.LEFTWARD);
    public static final BendingForm RIGHT = new BendingForm("right", Type.MOTION, Type.Motion.RIGHTWARD);
    public static final BendingForm RAISE = new BendingForm("raise", Type.MOTION, Type.Motion.UPWARD);
    public static final BendingForm LOWER = new BendingForm("lower", Type.MOTION, Type.Motion.DOWNWARD);
    public static final BendingForm ROTATE = new BendingForm("rotate", Type.MOTION);
    public static final BendingForm EXPAND = new BendingForm("expand", Type.SHAPE);
    public static final BendingForm COMPRESS = new BendingForm("compress", Type.SHAPE);
    public static final BendingForm SPLIT = new BendingForm("split", Type.SHAPE);
    public static final BendingForm COMBINE = new BendingForm("combine", Type.SHAPE);
    public static final BendingForm ARC = new BendingForm("arc", Type.INITIALIZER); // Trigger & charge motion Forms
    public static final BendingForm SHAPE = new BendingForm("shape", Type.INITIALIZER); // Trigger & charge shape Forms
    public static final BendingForm FOCUS = new BendingForm("focus", Type.INITIALIZER);

    public static void init() {}

    public static BendingForm get(String name) {
        return (BendingForm) Registries.getForm(name);
    }
}
