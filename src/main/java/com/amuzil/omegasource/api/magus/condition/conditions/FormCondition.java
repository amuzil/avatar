package com.amuzil.omegasource.api.magus.condition.conditions;

import com.amuzil.omegasource.api.magus.condition.Condition;
import com.amuzil.omegasource.bending.form.Form;
import com.amuzil.omegasource.events.FormActivatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.function.Consumer;


public class FormCondition extends Condition {
    private final Consumer<FormActivatedEvent> listener;
    private Form form;
    private boolean active;

    public FormCondition() {
        listener = event -> {
            form = event.getForm();
            active = !event.released();
            onSuccess.run();
        };
    }

    public Form form() {
        return form;
    }

    public boolean active() {
        return active;
    }

    @Override
    public void register(String name, Runnable onSuccess, Runnable onFailure) {
        super.register(name, onSuccess, onFailure);
        this.register();
    }

    @Override
    public void register() {
        super.register();
        //This is required because a class type check isn't built-in, for some reason.
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, listener);
    }

    @Override
    public void unregister() {
        super.unregister();
        MinecraftForge.EVENT_BUS.unregister(listener);
    }

    @Override
    public String name() {
        return "FormCondition";
    }
}
