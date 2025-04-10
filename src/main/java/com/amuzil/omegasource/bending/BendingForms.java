package com.amuzil.omegasource.bending;

import com.amuzil.omegasource.api.magus.form.Form;

public class BendingForms {
    // Basic Bending Forms

    public static final Form NULL = new Form("null"); // Blank Form
    public static final Form PUSH = new Form("push", Form.Type.MOTION);
    public static final Form PULL = new Form("pull", Form.Type.MOTION);
    public static final Form RAISE = new Form("raise", Form.Type.MOTION);
    public static final Form LOWER = new Form("lower", Form.Type.MOTION);
    public static final Form LEFT = new Form("left", Form.Type.MOTION);
    public static final Form RIGHT = new Form("right", Form.Type.MOTION);
    public static final Form ROTATE = new Form("rotate", Form.Type.MOTION);
    public static final Form EXPAND = new Form("expand", Form.Type.SHAPE);
    public static final Form COMPRESS = new Form("compress", Form.Type.SHAPE);
    public static final Form SPLIT = new Form("split", Form.Type.SHAPE);
    public static final Form COMBINE = new Form("combine", Form.Type.SHAPE);
    public static final Form PHASE = new Form("phase", Form.Type.INITIALIZER);
    public static final Form SHAPE = new Form("shape", Form.Type.INITIALIZER);
    public static final Form ARC = new Form("arc", Form.Type.INITIALIZER);
    public static final Form STRIKE = new Form("strike", Form.Type.DEFAULT);
    public static final Form BLOCK = new Form("block", Form.Type.DEFAULT);

    public static void init() {}
}
