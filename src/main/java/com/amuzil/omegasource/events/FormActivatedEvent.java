package com.amuzil.omegasource.events;

import com.amuzil.omegasource.bending.BendingForm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;


public class FormActivatedEvent extends Event {
    private final BendingForm form;
    private final LivingEntity entity;
    private final boolean released;

    public FormActivatedEvent(BendingForm form, LivingEntity entity) {
        this.form = form;
        this.entity = entity;
        this.released = false;
    }

    public FormActivatedEvent(BendingForm form, LivingEntity entity, boolean released) {
        this.form = form;
        this.entity = entity;
        this.released = released;
    }

    public BendingForm getForm() {
        return this.form;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }

    public boolean released() {
        return this.released;
    }
}
