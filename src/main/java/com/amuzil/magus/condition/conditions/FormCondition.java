package com.amuzil.magus.condition.conditions;

import com.amuzil.magus.form.FormActivatedEvent;
import com.amuzil.magus.condition.Condition;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;

import java.util.function.Consumer;


public class FormCondition extends Condition {
    private final Consumer<FormActivatedEvent> listener;

    public FormCondition() {
        listener = event -> {
            active = !event.released();
            onSuccess.run();
        };
    }

    @Override
    public void registerRunnables(String name, Runnable onSuccess, Runnable onFailure) {
        super.registerRunnables(name, onSuccess, onFailure);
        this.registerRunnables();
    }

    @Override
    public void registerRunnables() {
        super.registerRunnables();
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, listener);
    }

    @Override
    public void unregister() {
        super.unregister();
        NeoForge.EVENT_BUS.unregister(listener);
    }

    @Override
    public String name() {
        return "FormCondition";
    }
}