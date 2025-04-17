package com.amuzil.omegasource.api.magus.form;

import com.amuzil.omegasource.bending.BendingForm;
import com.amuzil.omegasource.registry.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;


public class ActiveForm {
    // TODO - Create way to save elapsedTime data server-side
    private BendingForm form;
    private boolean active;

    public ActiveForm(BendingForm form, boolean active) {
        this.form = form;
        this.active = active;
    }

    public BendingForm form() {
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
        return Objects.hash(form);// + Objects.hashCode(active);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof ActiveForm other)) {
            return false;
        } else {
            return form.name().equals(other.form.name());
        }
    }

    @Override
    public String toString() {
        return String.format("ActiveForm(%s, %b)", form.name(), active);
    }
}
