package com.amuzil.magus.form;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;


public class FormActivatedEvent extends Event {
    private final ActiveForm activeForm;
    private final LivingEntity entity;
    private final boolean released;

    public FormActivatedEvent(ActiveForm activeForm, LivingEntity entity) {
        this.activeForm = activeForm;
        this.entity = entity;
        this.released = false;
    }

    public FormActivatedEvent(ActiveForm activeForm, LivingEntity entity, boolean released) {
        this.activeForm = activeForm;
        this.entity = entity;
        this.released = released;
    }

    public ActiveForm getActiveForm() {
        return this.activeForm;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }

    public boolean released() {
        return this.released;
    }
}
