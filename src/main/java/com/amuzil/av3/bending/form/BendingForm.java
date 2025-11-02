package com.amuzil.av3.bending.form;

import com.amuzil.magus.form.Form;


public class BendingForm extends Form {
    private final Type type;
    private final Type.Motion direction;

    public BendingForm(String name, Type type, Type.Motion direction) {
        super(name);
        this.type = type;
        this.direction = direction;
    }

    public BendingForm(String name, Type type) {
        this(name, type, Type.Motion.NONE);
    }

    public BendingForm(String name) {
        this(name, Type.NONE, Type.Motion.NONE);
    }

    public BendingForm() {
        this(null, Type.NONE, Type.Motion.NONE);
    }

    @Override
    public String toString() {
        return "BendingForm[ " + name().toUpperCase() + " ]";
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof BendingForm other)) {
            return false;
        } else {
            return name.equals(other.name);
        }
    }

    public boolean notNull() {
        return !name.equals("null");
    }

    public Type type() {
        return type;
    }

    public Type.Motion direction() {
        return direction;
    }

    public enum Type {
        NONE,
        MOTION,
        SHAPE,
        INITIALIZER;

        public enum Motion {
            NONE,
            FORWARD,
            BACKWARD,
            LEFTWARD,
            RIGHTWARD,
            UPWARD,
            DOWNWARD
        }
    }
}
