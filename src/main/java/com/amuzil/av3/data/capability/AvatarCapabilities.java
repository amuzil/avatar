package com.amuzil.av3.data.capability;

import com.amuzil.av3.Avatar;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@EventBusSubscriber(modid = Avatar.MOD_ID)
public final class AvatarCapabilities {
    public static final ResourceLocation ID = Avatar.id("bender");
    public static final EntityCapability<Bender, Void> BENDER = EntityCapability.createVoid(ID, Bender.class);
    public static final Map<UUID, Bender> BENDER_CACHE = new HashMap<>();

    @SubscribeEvent
    private static void register(RegisterCapabilitiesEvent event) {
        event.registerEntity(BENDER, EntityType.PLAYER, (entity, ctx) -> new Bender(entity));
    }

    @SubscribeEvent
    public static void onPlayerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        syncBenderCap(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        removeCachedBender(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        syncBenderCap(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
        syncBenderCap(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerStartTrackingEvent(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof Player && event.getEntity() instanceof ServerPlayer)
            syncBenderCap(event.getEntity());
    }

    public static void syncBenderCap(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            Bender bender = getOrCreateBender(serverPlayer);
            if (bender != null && bender.isDirty())
                bender.syncToClient();
        }
    }

    public static Bender getOrCreateBender(Player player) {
        return BENDER_CACHE.computeIfAbsent(player.getUUID(), id -> Bender.getBender(player));
    }

    public static void removeCachedBender(Player player) {
        BENDER_CACHE.remove(player.getUUID());
    }
}
