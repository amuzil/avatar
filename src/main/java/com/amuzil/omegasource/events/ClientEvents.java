package com.amuzil.omegasource.events;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.capability.Bender;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = Avatar.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        Avatar.inputModule.registerListeners();
        System.out.println("InputModule Initiated for " + event.getPlayer().getName().getString());
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        if (Avatar.inputModule != null) {
            Avatar.inputModule.terminate();
        }
    }
}
