package com.amuzil.omegasource.events;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.capability.AvatarCapabilities;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.network.AvatarNetwork;
import com.amuzil.omegasource.network.packets.client.SyncBenderPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;


@Mod.EventBusSubscriber(modid = Avatar.MOD_ID)
public class ServerEvents {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Player)) return; // Ignore non-player entities

        if (event.getEntity() instanceof Player player) {
            Bender bender = (Bender) Bender.getBender(player);
            bender.registerFormCondition();
            System.out.println(player.getName().getString() + " registerFormCondition CLIENT-SIDE: " + event.getLevel().isClientSide());
        }
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
                CompoundTag tag = bender.serializeNBT();
                AvatarNetwork.sendToClient(new SyncBenderPacket(tag, serverPlayer.getUUID()), serverPlayer);
                System.out.println("EntityJoinLevelEvent SYNC SERVER TO CLIENT ON JOIN");
            });
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // TODO - Causes whole server to crash when player leaves
            //      java.lang.NullPointerException: Cannot invoke "com.amuzil.omegasource.magus.input.InputModule.getFormsTree()"
            //      because "com.amuzil.omegasource.magus.Magus.keyboardMouseInputModule" is null

            Bender bender = (Bender) Bender.getBender(player);
            assert bender != null;
            bender.unregisterFormCondition();
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;

        serverPlayer.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
            CompoundTag tag = bender.serializeNBT();
            AvatarNetwork.sendToClient(new SyncBenderPacket(tag, serverPlayer.getUUID()), serverPlayer);
            System.out.println("PlayerLoggedInEvent SYNC SERVER TO CLIENT ON JOIN");
        });
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;

        serverPlayer.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
            CompoundTag tag = bender.serializeNBT();
            AvatarNetwork.sendToClient(new SyncBenderPacket(tag, serverPlayer.getUUID()), serverPlayer);
            System.out.println("PlayerLoggedOutEvent SYNC SERVER TO CLIENT ON LEAVE");
        });
    }

    @SubscribeEvent
    public static void worldTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() != null && event.getEntity().isAlive()) {
            Bender bender = (Bender) Bender.getBender(event.getEntity());
            if (bender == null) return;
            bender.tick();
        }
    }
}