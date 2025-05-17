package com.amuzil.omegasource.api.magus.form;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

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
        if (activeForm.active() && complexForms.size() < 4) { // Limit to 4 active Forms
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

        // Serialize simpleForms
        ListTag simpleList = new ListTag();
        for (ActiveForm form : simpleForms) {
            simpleList.add(form.serializeNBT());
        }
        tag.put("SimpleForms", simpleList);

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
        simpleForms.clear();
        complexForms.clear();

        // Deserialize simpleForms
        ListTag simpleList = tag.getList("SimpleForms", Tag.TAG_COMPOUND);
        for (int i = 0; i < simpleList.size(); i++) {
            ActiveForm form = new ActiveForm(simpleList.getCompound(i));
            simpleForms.add(form);
        }

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
        return Objects.hash(simpleForms, complexForms);
    }

    @Override
    public String toString() {
        return "FormPath{" +
                "simpleForms=" + simpleForms +
                ", complexForms=" + complexForms +
                ", active=" + active +
                '}';
    }
}
