package com.amuzil.av3.utils.sound;

import com.amuzil.av3.Avatar;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

// Avatar Sound Effects
public class AvatarSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, Avatar.MOD_ID);

    public static final Supplier<SoundEvent> AIR_GUST = register("air_gust");

    public static final Supplier<SoundEvent> FIRE_STRIKE = register("fires_bloom_perma5");

    private static Supplier<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name,
                () -> SoundEvent.createVariableRangeEvent(Avatar.id(name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
