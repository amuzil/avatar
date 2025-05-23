package com.amuzil.omegasource.api.magus.form;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.bending.form.BendingForm;
import com.amuzil.omegasource.api.magus.registry.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;


public class ActiveForm {

    private BendingForm form;
    private boolean active;
    private BendingForm.Type.Motion motion = BendingForm.Type.Motion.NONE;

    public ActiveForm(String formName, boolean active) {
        this(
            (BendingForm) Registries.FORMS.get().getValue(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, formName)),
            active
        );
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
        form = (BendingForm) Registries.FORMS.get().getValue(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, tag.getString("Form")));
        active = tag.getBoolean("Active");
        motion = BendingForm.Type.Motion.valueOf(tag.getString("Direction"));
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
            return form.name().equals(other.form.name());
        }
    }

    @Override
    public String toString() {
        return String.format("ActiveForm(%s, %b)", form.name(), active);
    }
}
