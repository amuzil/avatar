package com.amuzil.omegasource.bending;

import com.amuzil.omegasource.api.magus.form.Form;
import com.amuzil.omegasource.registry.Registries;


public class BendingForm extends Form {
    private final String name;
    private final Type type;

    public BendingForm(String name, Type type) {
        super(name);
        this.name = name;
        this.type = type;
        Registries.registerForm(this);
    }

    public BendingForm(String name) {
        this.name = name;
        this.type = Type.NONE;
    }

    public BendingForm() { // Create null Form to fix random NullPointerException
        this.name = null;
        this.type = Type.NONE;
    }

    public String name() {
        return name;
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
