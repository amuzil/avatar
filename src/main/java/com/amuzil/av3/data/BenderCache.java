package com.amuzil.av3.data;

import com.amuzil.av3.data.capability.Bender;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class BenderCache {
    private final Map<UUID, Bender> cache = new HashMap<>();

    public Bender get(ServerPlayer player) {
        return cache.computeIfAbsent(player.getUUID(), id -> new Bender(player));
    }

    public Bender remove(ServerPlayer player) {
        return cache.remove(player.getUUID());
    }

    public void clear() {
        cache.clear();
    }
}
