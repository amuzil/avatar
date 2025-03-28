package com.amuzil.omegasource.api.magus.condition.conditions;

import com.amuzil.omegasource.api.magus.condition.Condition;
import com.amuzil.omegasource.bending.form.Form;
import com.amuzil.omegasource.events.FormActivatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.function.Consumer;


public class FormCondition extends Condition {
    private final Consumer<FormActivatedEvent> listener;
    private final Consumer<TickEvent> tickListener;
    private Form form;
    private boolean active;
    private final int timeout = 300; // Adjust timeout time here
    private int tick = timeout;

    public FormCondition() {
        listener = event -> {
            form = event.getForm();
            active = !event.released();
            onSuccess.run();
        };

        tickListener = event -> {
            // Ticking for both server & client ~40 ticks == 1 second
            if (!active) {
                if (tick == 0) {
                    onFailure.run();
                    tick = timeout;
                    active = true;
                }
                tick--;
            }
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
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, tickListener);
    }

    @Override
    public void unregister() {
        super.unregister();
        MinecraftForge.EVENT_BUS.unregister(listener);
        MinecraftForge.EVENT_BUS.unregister(tickListener);
    }

    @Override
    public String name() {
        return "FormCondition";
    }
}
