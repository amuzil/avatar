package com.amuzil.av3.events;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.data.capability.AvatarCapabilities;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.entity.construct.PhysicsBenderEntity;
import com.amuzil.magus.skill.event.SkillTickEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;


@EventBusSubscriber(modid = Avatar.MOD_ID)
public class ServerEvents {

//    @SubscribeEvent
//    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
//        if (!(event.getEntity() instanceof Player)) return; // Ignore non-player entities
//
//        if (event.getEntity() instanceof ServerPlayer player) {
//            Bender bender = AvatarCapabilities.getOrCreateBender(player);
//            if (bender == null) return;
//            bender.register();
//        }
//    }

//    @SubscribeEvent
//    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
//        if (!(event.getEntity() instanceof Player)) return; // Ignore non-player entities
//
//        if (event.getEntity() instanceof ServerPlayer player) {
//            Bender bender = AvatarCapabilities.getOrCreateBender(player);
//            AvatarCapabilities.removeCachedBender(player);
//            if (bender == null) return;
//            bender.unregister();
//        }
//    }

    @SubscribeEvent
    public static void onPlayerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        Bender bender = AvatarCapabilities.syncBender(event.getEntity());
//        ServerPlayer player = (ServerPlayer) event.getEntity();
//        PhysicsBenderEntity physicsBenderEntity = new PhysicsBenderEntity(player.level());
//        physicsBenderEntity.setOwner(player);
//        player.level().addFreshEntity(physicsBenderEntity);
//        bender.physicsBenderEntity = physicsBenderEntity;
        if (bender == null) return;
        bender.register();
    }

    @SubscribeEvent
    public static void onPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        Bender bender = AvatarCapabilities.removeCachedBender(event.getEntity());
        if (bender == null) return;
        bender.unregister();
    }

    @SubscribeEvent
    public static void onPlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        AvatarCapabilities.syncBender(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
        AvatarCapabilities.syncBender(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerStartTrackingEvent(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof Player && event.getEntity() instanceof ServerPlayer)
            AvatarCapabilities.syncBender(event.getEntity());
    }

//    @SubscribeEvent
//    public static void onBlockBreak(BlockEvent.BreakEvent event) {
//        Bender bender = AvatarCapabilities.getOrCreateBender(event.getPlayer());
//        if (bender == null) return;
//        if (bender.getElement() == Elements.EARTH) {
//            event.setCanceled(true);
//        }
//    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!player.isAlive()) return;

        Bender bender = AvatarCapabilities.getOrCreateBender(player);
        bender.tick();
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Pre event) {
        NeoForge.EVENT_BUS.post(new SkillTickEvent());
    }

}