package com.amuzil.omegasource.api.magus.form;

import net.minecraft.nbt.CompoundTag;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public class FormPath {

    private final List<ActiveForm> simpleForms; // Only populated with currently active Forms (removed on key release)
    private final List<ActiveForm> complexForms; // Only populated with recent active Forms (removed on timeout)
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

    public FormPath(List<ActiveForm> simpleForms, List<ActiveForm> complexForms) {
        this.simpleForms = simpleForms;
        this.complexForms = complexForms;
    }

    public void clear() {
        complexForms.clear();
    }

    public void clearAll() {
        simpleForms.clear();
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

    @Override
    public int hashCode() {
        return Objects.hash(simpleForms, complexForms);

    }
}
