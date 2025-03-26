package com.amuzil.omegasource.events;

import com.amuzil.omegasource.bending.form.Form;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;


public class FormActivatedEvent extends Event {
    private final Form form;
    private final LivingEntity entity;
    private final boolean released;

    public FormActivatedEvent(Form form, LivingEntity entity) {
        this.form = form;
        this.entity = entity;
        this.released = false;
    }

    public FormActivatedEvent(Form form, LivingEntity entity, boolean released) {
        this.form = form;
        this.entity = entity;
        this.released = released;
    }

    public Form getForm() {
        return this.form;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }

    public boolean released() {
        return this.released;
    }
}
