package com.amuzil.omegasource.api.magus.form;

import com.amuzil.omegasource.registry.Registries;


public class Form {
    private final String name;
    private final Type type;

    public Form(String name, Type type) {
        this.name = name;
        this.type = type;
        Registries.registerForm(this);
    }

    public Form() { // Create null Form to fix random NullPointerException
        this.name = null;
        this.type = Type.DEFAULT;
    }

    public Form(String name) {
        this.name = name;
        this.type = Type.DEFAULT;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return "Form[ " + name().toUpperCase() + " ]";
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Form other)) {
            return false;
        } else {
            return name.equals(other.name);
        }
    }

    public Form.Type type() {
        return type;
    }

    public enum Type {
        MOTION, SHAPE, PHASE, DEFAULT, INITIALIZER
    }
}
