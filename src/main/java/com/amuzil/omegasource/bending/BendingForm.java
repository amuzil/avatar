package com.amuzil.omegasource.bending;

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
        super(null); // Create null Form to fix random NullPointerException
        this.type = Type.NONE;
    }

    public String name() {
        return super.name();
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

    public BendingForm.Type type() {
        return type;
    }

    public enum Type {
        NONE,
        MOTION,
        SHAPE,
        INITIALIZER
    }
}
