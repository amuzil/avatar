package com.amuzil.omegasource.events;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.capability.AvatarCapabilities;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = Avatar.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockBreakHandler {
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        System.out.println("Block broken!");
        event.getPlayer().getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
            if (bender.getElement() == Elements.EARTH) {
                event.setCanceled(true);
            }
        });
    }
}
