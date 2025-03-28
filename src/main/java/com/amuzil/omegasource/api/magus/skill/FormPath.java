package com.amuzil.omegasource.api.magus.skill;

import com.amuzil.omegasource.bending.form.ActiveForm;
import net.minecraft.nbt.CompoundTag;

import java.util.LinkedList;
import java.util.List;

public class FormPath {

    private List<ActiveForm> simpleForms;
    private List<ActiveForm> complexForms;
    private boolean active;

    public FormPath() {
        this.simpleForms = new LinkedList<>();
        this.complexForms = new LinkedList<>();
        this.active = false;
    }

    public FormPath(List<ActiveForm> complexForms) {
        this.simpleForms = new LinkedList<>();
        this.complexForms = complexForms;
    }

    public void clear() {
        complexForms.clear();
    }

    public void update(ActiveForm activeForm) {
        if (activeForm.active()) {
            simpleForms.add(activeForm);
            complexForms.add(activeForm);
        } else {
            simpleForms.remove(activeForm);
        }
        active = !simpleForms.isEmpty();
    }

    public boolean isActive() {
        return this.active;
    }

    public List<ActiveForm> simple() {
        return this.simpleForms;
    }

    public List<ActiveForm> complex() {
        return this.complexForms;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        int i = 0;
        for (ActiveForm form : simpleForms) {
            tag.put("Active " + i, form.serializeNBT());
            i++;
        }
        tag.putInt("Active Size", i);
        return tag;
    }

    public void deserializeNBT(CompoundTag compoundTag) {
        int size = compoundTag.getInt("Active Size");
        for (int i = 0; i < size; i++) {
            simpleForms.get(i).deserializeNBT((CompoundTag) compoundTag.get("Active " + i));
        }
    }
}
