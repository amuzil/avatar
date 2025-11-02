package com.amuzil.av3.api.magus.condition.conditions;

import com.amuzil.av3.api.magus.condition.Condition;
import com.amuzil.av3.events.FormActivatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

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