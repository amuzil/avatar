package com.amuzil.av3.data.capability;

import com.amuzil.av3.Avatar;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

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

    public static Bender syncBender(Player player) {
        Bender bender = getOrCreateBender(player);
        if (bender != null && bender.isDirty())
            bender.syncToClient();
        return bender;
    }

    public static Bender getOrCreateBender(Player player) {
        return BENDER_CACHE.computeIfAbsent(player.getUUID(), id -> Bender.getBender(player));
    }

    public static Bender removeCachedBender(Player player) {
        return BENDER_CACHE.remove(player.getUUID());
    }
}
