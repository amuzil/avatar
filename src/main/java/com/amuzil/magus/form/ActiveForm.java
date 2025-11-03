package com.amuzil.magus.form;

import com.amuzil.av3.Avatar;
import com.amuzil.magus.registry.Registries;
import com.amuzil.av3.bending.form.BendingForm;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;


public class ActiveForm {

    private BendingForm form;
    private boolean active;
    private BendingForm.Type.Motion motion = BendingForm.Type.Motion.NONE;

    public ActiveForm(String formName, boolean active) {
        this((BendingForm) Registries.FORMS.get().getValue(
                Avatar.id(formName)), active);
    }

    public ActiveForm(BendingForm form, boolean active) {
        this.form = form;
        this.active = active;
    }

    public ActiveForm(CompoundTag tag) {
        this.deserializeNBT(tag);
    }

    public BendingForm form() {
        return form;
    }

    public boolean active() {
        return active;
    }

    public BendingForm.Type.Motion direction() {
        return motion;
    }

    public void setDirection(BendingForm.Type.Motion motion) {
        this.motion = motion;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Form", this.form.name());
        tag.putBoolean("Active", this.active());
        tag.putString("Direction", this.motion.name());
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        form = (BendingForm) Registries.FORMS.get().getValue(Avatar.id(tag.getString("Form")));
        active = tag.getBoolean("Active");
        motion = BendingForm.Type.Motion.valueOf(tag.getString("Direction"));
    }

    @Override
    public int hashCode() {
        return Objects.hash(form, active);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof ActiveForm other)) {
            return false;
        } else {
            return hashCode() == obj.hashCode();
        }
    }

    @Override
    public String toString() {
        return String.format("ActiveForm(%s, %b)", form.name(), active);
    }
}
