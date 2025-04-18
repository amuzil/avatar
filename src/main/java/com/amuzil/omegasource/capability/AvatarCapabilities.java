package com.amuzil.omegasource.capability;

import com.amuzil.omegasource.Avatar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = Avatar.MOD_ID)
public class AvatarCapabilities {
    public static final Capability<IBender> BENDER = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IBender.class);
    }

    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            BenderProvider provider = new BenderProvider();
            event.addCapability(BenderProvider.ID, provider);
            event.addListener(provider::invalidate);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        player.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
            CompoundTag capData = bender.serializeNBT();
            player.getPersistentData().put("BenderCap", capData);
        });
    }

    // Clone data on respawn
    @SubscribeEvent
    public static void onClonePlayer(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        CompoundTag capData = event.getOriginal().getPersistentData().getCompound("BenderCap");
        System.out.println("CLONING -> "+ capData + " -> " + capData.get("Element"));
        event.getEntity().getCapability(AvatarCapabilities.BENDER).ifPresent(newCap -> {
            newCap.deserializeNBT(capData);
        });
    }
}
