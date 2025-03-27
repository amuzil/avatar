package com.amuzil.omegasource.api.magus.skill;

import com.amuzil.omegasource.bending.form.ActiveForm;
import com.amuzil.omegasource.registry.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.units.qual.A;

import java.util.LinkedList;
import java.util.List;

public class FormPath {

    private List<ActiveForm> simple;
    private List<ActiveForm> complex;
    public void clear() {
        if (simple == null)
            simple = new LinkedList<>();
        if (complex == null)
            complex = new LinkedList<>();
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

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        int i = 0;
        for (ActiveForm form : complex()) {
            tag.put("Complex " + i, form.serializeNBT());
            i++;
        }
        tag.putInt("Complex Size", i);
        i = 0;
        for (ActiveForm form : simple()) {
            tag.put("Simple " + i, form.serializeNBT());
            i++;
        }
        tag.putInt("Simple Size", i);
        return tag;
    }

    public void deserializeNBT(CompoundTag compoundTag) {
        int size = compoundTag.getInt("Complex Size");
        for (int i = 0; i < size; i++) {
            complex.get(i).deserializeNBT((CompoundTag) compoundTag.get("Complex " + i));
        }

        size = compoundTag.getInt("Simple Size");
        for (int i = 0; i < size; i++) {
            simple.get(i).deserializeNBT((CompoundTag) compoundTag.get("Simple " + i));
        }
    }
}
