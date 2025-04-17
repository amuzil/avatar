package com.amuzil.omegasource.api.magus.form;


public class Form {
    private final String name;

    public Form(String name) {
        this.name = name;
    }

    public Form() { // Create null Form to fix random NullPointerException
        this.name = null;
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

    @Override
    public int hashCode() {
        return name().hashCode();
    }
}

