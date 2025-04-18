package com.amuzil.omegasource.capability;

import com.amuzil.omegasource.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
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

    // Clone data on respawn
    @SubscribeEvent
    public static void onClonePlayer(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        LazyOptional<IBender> oldCapOpt = event.getOriginal().getCapability(AvatarCapabilities.BENDER);
        LazyOptional<IBender> newCapOpt = event.getEntity().getCapability(AvatarCapabilities.BENDER);
        System.out.println("[Bender] Death clone event");

        if (oldCapOpt.isPresent()) {
            IBender oldCap = oldCapOpt.orElse(null); // safe now
            System.out.println("[Bender] Found old capability with element: " + oldCap.getElement());

            if (newCapOpt.isPresent()) {
                IBender newCap = newCapOpt.orElse(null);
                System.out.println("[Bender] Found new capability before cloning: " + newCap.getElement());

                // You can either do this...
                newCap.setElement(oldCap.getElement());

                // Or more generally (preferred if you add more fields later)
                // newCap.deserializeNBT(oldCap.serializeNBT());

                System.out.println("[Bender] Set new capability element to: " + oldCap.getElement());
            } else {
                System.out.println("[Bender] Could NOT find new player capability!");
            }
        } else {
            System.out.println("[Bender] Could NOT find old player capability!");
        }
    }

}
