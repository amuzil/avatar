package com.amuzil.omegasource.bending.form;


import com.amuzil.omegasource.registry.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;


public class ActiveForm {
    private Form form;
    private boolean active;

    public ActiveForm(Form form, boolean active) {
        this.form = form;
        this.active = active;
    }

    public Form form() {
        return form;
    }

    public boolean active() {
        return active;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Form", this.form.name());
        tag.putBoolean("Active", this.active());
        return tag;
    }

    public void deserializeNBT(CompoundTag compoundTag) {
        form = Registries.FORMS.get().getValue(ResourceLocation.tryParse(compoundTag.getString("Form")));
        active = compoundTag.getBoolean("Active");
    }

    @Override
    public int hashCode() {
        return Objects.hash(form);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof ActiveForm other)) {
            return false;
        } else {
            return form.name().equals(other.form.name()) && active == other.active;
        }
    }

    @Override
    public String toString() {
        return String.format("ActiveForm(%s, %b)", form.name(), active);
    }
}
