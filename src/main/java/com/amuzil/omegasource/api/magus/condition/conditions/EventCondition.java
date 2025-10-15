package com.amuzil.omegasource.api.magus.condition.conditions;

import com.amuzil.omegasource.api.magus.condition.Condition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.function.Consumer;
import java.util.function.Function;


public class EventCondition<E extends Event> extends Condition {
    private final Consumer<E> listener;
    private final Class<E> eventType;

    public EventCondition(Class<E> eventType, Function<E, Boolean> condition) {
        this.eventType = eventType;
        this.listener = event -> {
            if (condition.apply(event)) {
                this.onSuccess.run();
            } else {
                this.onFailure.run();
            }
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
        //This is required because a class type check isn't built-in, for some reason.
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, eventType, listener);
    }

    @Override
    public void unregister() {
        super.unregister();
        MinecraftForge.EVENT_BUS.unregister(listener);
    }

    @Override
    public String name() {
        return "event_trigger";
    }
}
