package com.amuzil.av3.capability;

import com.amuzil.av3.Avatar;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Map;
import java.util.WeakHashMap;


@EventBusSubscriber(modid = Avatar.MOD_ID)
public final class AvatarCapabilities {
    public static final ResourceLocation ID = Avatar.id("bender");
    public static final EntityCapability<IBender, Void> BENDER = EntityCapability.createVoid(ID, IBender.class);

    // Persistent cache of player → capability instance
    private static final Map<Player, IBender> BENDER_CACHE = new WeakHashMap<>();

    @SubscribeEvent
    private static void register(RegisterCapabilitiesEvent event) {
        event.registerEntity(BENDER, EntityType.PLAYER,
                (entity, ctx) -> BENDER_CACHE.computeIfAbsent(entity, Bender::new)
        );

//        BuiltInRegistries.ENTITY_TYPE.stream().
//                filter(type -> Player.class.isAssignableFrom(type.getBaseClass())).
//                map(type -> (EntityType<Player)> type).
//                forEach(type ->
//                        event.registerEntity(BENDER, type,
//                                (entity, context) -> new Bender(entity)));
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        BENDER_CACHE.remove(event.getEntity());
    }

    // Save data on death
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        HolderLookup.Provider lookup = player.level().registryAccess();
        player.getPersistentData().put("BenderCap", player.getCapability(BENDER).serializeNBT(lookup));
    }

    // Clone data on respawn
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        CompoundTag capData = event.getOriginal().getPersistentData().getCompound("BenderCap");
        HolderLookup.Provider lookup = event.getEntity().level().registryAccess();
        event.getEntity().getCapability(BENDER).deserializeNBT(lookup, capData);
    }
}
