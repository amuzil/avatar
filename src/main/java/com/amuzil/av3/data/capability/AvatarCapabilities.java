package com.amuzil.av3.data.capability;

import com.amuzil.av3.Avatar;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;


@EventBusSubscriber(modid = Avatar.MOD_ID)
public final class AvatarCapabilities {
    public static final ResourceLocation ID = Avatar.id("bender");
    public static final EntityCapability<Bender, Void> BENDER = EntityCapability.createVoid(ID, Bender.class);

    @SubscribeEvent
    private static void register(RegisterCapabilitiesEvent event) {
        event.registerEntity(BENDER, EntityType.PLAYER, (entity, ctx) -> new Bender(entity));
    }

    public static Bender syncBender(ServerPlayer player) {
        Bender bender = getBender(player);
        if (bender != null && bender.isDirty())
            bender.syncToClient();
        return bender;
    }

    public static Bender getBender(ServerPlayer player) {
        return Avatar.BENDER_CACHE.get(player);
    }
}
