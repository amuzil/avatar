package com.amuzil.omegasource.api.magus.skill;

import com.amuzil.omegasource.bending.form.ActiveForm;
import net.minecraft.nbt.CompoundTag;

import java.util.LinkedList;
import java.util.List;

public class FormPath {

    private List<ActiveForm> active;
    public void clear() {
        if (active == null)
            active = new LinkedList<>();
        active.clear();
    }

    public FormPath() {
        this.active = new LinkedList<>();
    }

    public FormPath(List<ActiveForm> complexForms) {
        this.active = complexForms;
    }
    public void add(List<ActiveForm> complex) {
        this.active = complex;
    }

    public List<ActiveForm> active() {
        return this.active;
    }


    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        int i = 0;
        for (ActiveForm form : active) {
            tag.put("Active " + i, form.serializeNBT());
            i++;
        }
        tag.putInt("Active Size", i);
        return tag;
    }

    public void deserializeNBT(CompoundTag compoundTag) {
        int size = compoundTag.getInt("Active Size");
        for (int i = 0; i < size; i++) {
            active.get(i).deserializeNBT((CompoundTag) compoundTag.get("Active " + i));
        }
    }
}
