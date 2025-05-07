package com.amuzil.omegasource.bending.form;

import com.amuzil.omegasource.api.magus.form.Form;


public class BendingForm extends Form {
    private final Type type;

    public BendingForm(String name, Type type) {
        super(name);
        this.type = type;
    }

    public BendingForm(String name) {
        this(name, Type.NONE);
    }

    public BendingForm() {
        super(null);
        this.type = Type.NONE;
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

    public String name() {
        return super.name();
    }

    public BendingForm.Type type() {
        return type;
    }

    public BendingForm.Type.Direction direction() {
        return type.direction;
    }

    public enum Type {
        NONE,
        MOTION,
        SHAPE,
        INITIALIZER;

        Direction direction = Direction.NONE;

        Type subType(Direction direction) {
            this.direction = direction;
            return this;
        }

        public enum Direction {
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
