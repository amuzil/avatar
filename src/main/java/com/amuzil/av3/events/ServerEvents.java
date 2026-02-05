package com.amuzil.av3.events;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.BendingSkill;
import com.amuzil.av3.data.capability.AvatarCapabilities;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.network.AvatarNetwork;
import com.amuzil.av3.network.packets.bending.ToggleBendingPacket;
import com.amuzil.av3.utils.commands.AvatarCommands;
import com.amuzil.magus.registry.Registries;
import com.amuzil.magus.skill.event.SkillTickEvent;
import com.amuzil.magus.tree.SkillTree;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;


@EventBusSubscriber(modid = Avatar.MOD_ID)
public class ServerEvents {

//    @SubscribeEvent
//    private static void onEntityJoinLevel(EntityJoinLevelEvent event) {
//        if (!(event.getEntity() instanceof Player)) return; // Ignore non-player entities
//
//        if (event.getEntity() instanceof ServerPlayer player) {
//            Bender bender = AvatarCapabilities.getOrCreateBender(player);
//            if (bender == null) return;
//            bender.register();
//        }
//    }

//    @SubscribeEvent
//    private static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
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
    private static void onServerStarting(ServerStartingEvent event) {
        Avatar.LOGGER.info("Setting up Avatar Mod server-side...");
        AvatarCapabilities.initBenderCache();
        AvatarCommands.register(event.getServer().getCommands().getDispatcher());

        // Initialize Skill Tree
        SkillTree.clear();
        Registries.SKILLS.stream().forEach(skill -> {
            SkillTree.RegisterSkill(((BendingSkill) skill).element(), /* toRegister.targetType(), */
                    skill.startPaths(), skill);
        });
    }

    @SubscribeEvent
    private static void onServerStopping(ServerStoppingEvent event) {
        AvatarCapabilities.clearBenderCache();
    }

    @SubscribeEvent
    private static void onPlayerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Bender bender = AvatarCapabilities.syncBender(player);
            if (bender == null) return;
            bender.register();
            AvatarNetwork.sendToClient(new ToggleBendingPacket(player.getUUID(), bender.isBending()), player);
        }
    }

    @SubscribeEvent
    private static void onPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Bender bender = AvatarCapabilities.removeBender(player);
            if (bender == null) return;
            bender.unregister();
        }
    }

    @SubscribeEvent
    private static void onPlayerCloneEvent(PlayerEvent.Clone event) {
        if (event.getOriginal() instanceof ServerPlayer oldPlayer &&
                event.getEntity() instanceof ServerPlayer newPlayer) {

            Bender oldBender = AvatarCapabilities.removeBender(oldPlayer);
            Bender newBender = AvatarCapabilities.getBender(newPlayer);
            if (oldBender == null || newBender == null) return;
            oldBender.unregister();
            newBender.register();
        }
    }

    @SubscribeEvent
    private static void onPlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player)
            AvatarCapabilities.syncBender(player);
    }

    @SubscribeEvent
    private static void onPlayerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Bender bender = AvatarCapabilities.syncBender(player);
            if (bender == null) return;
            AvatarNetwork.sendToClient(new ToggleBendingPacket(player.getUUID(), bender.isBending()), player);
        }
    }

    @SubscribeEvent
    private static void onPlayerStartTrackingEvent(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof Player && event.getEntity() instanceof ServerPlayer player)
            AvatarCapabilities.syncBender(player);
    }

//    @SubscribeEvent
//    private static void onBlockBreak(BlockEvent.BreakEvent event) {
//        Bender bender = AvatarCapabilities.getOrCreateBender(event.getPlayer());
//        if (bender == null) return;
//        if (bender.getElement() == Elements.EARTH) {
//            event.setCanceled(true);
//        }
//    }

    @SubscribeEvent
    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!player.isAlive()) return;

        Bender bender = AvatarCapabilities.getBender(player);
        bender.tick();
    }

    @SubscribeEvent
    private static void onServerTick(ServerTickEvent.Pre event) {
        NeoForge.EVENT_BUS.post(new SkillTickEvent());
    }

}