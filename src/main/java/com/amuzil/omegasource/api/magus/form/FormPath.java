package com.amuzil.omegasource.api.magus.form;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public class FormPath {

    private final List<ActiveForm> complexForms; // Only populated with recent active Forms (removed on timeout)
    private boolean active;

    public FormPath() {
        this.complexForms = new LinkedList<>();
        this.active = false;
    }

    public FormPath(List<ActiveForm> complexForms) {
        this.complexForms = complexForms;
    }

    public void clear() {
        complexForms.clear();
        active = false;
    }

    public void update(ActiveForm activeForm) {
        if (activeForm.active() && complexForms.size() < 4) { // Limit to 4 active Forms
            complexForms.add(activeForm);
        }
        active = complexForms.stream().anyMatch(c -> c.active());
    }

    public boolean isActive() {
        return this.active;
    }

    public List<ActiveForm> complex() {
        return this.complexForms;
    }

    public List<Form> forms() {
        return this.complexForms.stream().map(c -> (Form)c.form()).toList();
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        // Serialize simpleForms
        ListTag simpleList = new ListTag();

        // Serialize complexForms
        ListTag complexList = new ListTag();
        for (ActiveForm form : complexForms) {
            complexList.add(form.serializeNBT());
        }
        tag.put("ComplexForms", complexList);

        // Serialize active flag
        tag.putBoolean("Active", active);

        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        complexForms.clear();

        // Deserialize complexForms
        ListTag complexList = tag.getList("ComplexForms", Tag.TAG_COMPOUND);
        for (int i = 0; i < complexList.size(); i++) {
            ActiveForm form = new ActiveForm(complexList.getCompound(i));
            complexForms.add(form);
        }

        // Deserialize active flag
        active = tag.getBoolean("Active");
    }

    @Override
    public int hashCode() {
        return Objects.hash(complexForms);
    }

    @Override
    public String toString() {
        return "FormPath{" +
                ", complexForms=" + complexForms +
                ", active=" + active +
                '}';
    }
}
