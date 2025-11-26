package com.amuzil.av3.events;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.input.InputModule;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

import static com.amuzil.av3.data.attachment.AvatarAttachments.IS_BENDING;

@EventBusSubscriber(modid = Avatar.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        Avatar.LOGGER.info("Setting up Avatar Mod client-side...");
        Avatar.INPUT_MODULE = new InputModule();
        boolean isBending = event.getPlayer().getData(IS_BENDING);
        if (isBending)
            Avatar.INPUT_MODULE.registerListeners();
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        if (Avatar.INPUT_MODULE != null) {
            Avatar.INPUT_MODULE.terminate();
            Avatar.INPUT_MODULE = null;
        }
    }
}
