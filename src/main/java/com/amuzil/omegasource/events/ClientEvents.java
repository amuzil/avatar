package com.amuzil.omegasource.events;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.capability.Bender;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = Avatar.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

//    @SubscribeEvent
//    public static void onClientLogin(PlayerEvent.PlayerLoggedInEvent event) {}

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        if(event.getPlayer() != null) {
            Bender bender = (Bender) Bender.getBender(event.getPlayer());
            if (bender != null)
                bender.unregisterFormCondition();
        }
        if (Avatar.inputModule != null) {
            Avatar.inputModule.terminate();
            System.out.println("Unregistering CLIENT-SIDE");
        }
    }
}
