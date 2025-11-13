package com.amuzil.av3.events;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.element.Elements;
import com.amuzil.av3.data.capability.AvatarCapabilities;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.magus.skill.event.SkillTickEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;


@EventBusSubscriber(modid = Avatar.MOD_ID)
public class ServerEvents {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Player)) return; // Ignore non-player entities

        if (event.getEntity() instanceof ServerPlayer player) {
            Bender bender = AvatarCapabilities.getOrCreateBender(player);
            if (bender == null) return;
            bender.register();
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (!(event.getEntity() instanceof Player)) return; // Ignore non-player entities

        if (event.getEntity() instanceof ServerPlayer player) {
            Bender bender = AvatarCapabilities.getOrCreateBender(player);
            AvatarCapabilities.removeCachedBender(player);
            if (bender == null) return;
            bender.unregister();
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Bender bender = AvatarCapabilities.getOrCreateBender(event.getPlayer());
        if (bender == null) return;
        if (bender.getElement() == Elements.EARTH) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void worldTick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!player.isAlive()) return;

        Bender bender = AvatarCapabilities.getOrCreateBender(player);
        if (bender == null) return;
        bender.tick();
        NeoForge.EVENT_BUS.post(new SkillTickEvent());
    }
}