package com.amuzil.omegasource.api.magus.skill;

import com.amuzil.omegasource.bending.form.ActiveForm;
import org.checkerframework.checker.units.qual.A;

import java.util.LinkedList;
import java.util.List;

public class FormPath {

    private List<ActiveForm> simple;
    private List<ActiveForm> complex;
    public void clear() {
        simple.clear();
        complex.clear();
    }

    public FormPath() {
        this.simple = new LinkedList<>();
        this.complex = new LinkedList<>();
    }

    public FormPath(List<ActiveForm> simpleForms, List<ActiveForm> complexForms) {
        this.simple = simpleForms;
        this.complex = complexForms;
    }
    public void simple(List<ActiveForm> simple) {
        this.simple = simple;
    }

    public void complex(List<ActiveForm> complex) {
        this.complex = complex;
    }

    public List<ActiveForm> complex() {
        return this.complex;
    }

    public List<ActiveForm> simple() {
        return this.simple;
    }
}
