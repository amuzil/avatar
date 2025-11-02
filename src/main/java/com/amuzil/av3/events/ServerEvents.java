package com.amuzil.av3.events;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.api.magus.skill.event.SkillTickEvent;
import com.amuzil.av3.bending.element.Elements;
import com.amuzil.av3.capability.AvatarCapabilities;
import com.amuzil.av3.capability.Bender;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = Avatar.MOD_ID)
public class ServerEvents {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Player)) return; // Ignore non-player entities

        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
                bender.syncToClient();
                bender.register();
            });
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
                bender.syncToClient();
                bender.unregister();
            });
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        player.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
            bender.syncToClient();
            System.out.println("PlayerLoggedInEvent SYNC SERVER TO CLIENT ON JOIN");
        });
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        player.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
            bender.syncToClient();
            System.out.println("PlayerLoggedOutEvent SYNC SERVER TO CLIENT ON LEAVE");
        });
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        event.getPlayer().getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
            if (bender.getElement() == Elements.EARTH) {
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent
    public static void worldTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!player.isAlive()) return;
        Bender bender = (Bender) Bender.getBender(event.getEntity());
        if (bender == null) return;

        bender.tick();
        MinecraftForge.EVENT_BUS.post(new SkillTickEvent());
    }
}