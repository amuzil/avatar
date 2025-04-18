package com.amuzil.omegasource.server;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.capability.CapabilityHandler;
import com.amuzil.omegasource.api.magus.capability.entity.Data;
import com.amuzil.omegasource.api.magus.capability.entity.LivingDataCapability;
import com.amuzil.omegasource.network.AvatarNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import com.amuzil.omegasource.api.magus.capability.entity.Magi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;


@Mod.EventBusSubscriber(modid = Avatar.MOD_ID)
public class ServerEvents {

    @SubscribeEvent
    public static void worldStart(LevelEvent event) {}


    @SubscribeEvent
    public static void onServerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = event.getEntity();
        // Send a packet with the player’s current capability NBT
        AvatarNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncCapabilityPacket(
                        player.getCapability(CapabilityHandler.LIVING_DATA)
                                .map(Data::serializeNBT)
                                .orElse(new CompoundTag())
                )
        );
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        // Only copy when the player respawns from death
        if (!event.isWasDeath()) return;

        // Ensure the old player's caps are still valid
        event.getOriginal().reviveCaps();                                   // :contentReference[oaicite:0]{index=0}

        // Copy the NBT from the old instance to the new one
        event.getOriginal().getCapability(CapabilityHandler.LIVING_DATA)
                .ifPresent(oldCap -> event.getEntity().getCapability(CapabilityHandler.LIVING_DATA)
                        .ifPresent(newCap -> newCap.deserializeNBT(oldCap.serializeNBT()))
                );

        // Clean up the old instance’s caps
        event.getOriginal().invalidateCaps();                                // :contentReference[oaicite:1]{index=1}
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return; // Ignore non-player entities
        }

        if (event.getEntity() instanceof Player player) {
            Data data = CapabilityHandler.getCapability(player, CapabilityHandler.LIVING_DATA);
            if (data != null) {
                Magi magi = Magi.get(player);
                if (magi != null) {
                    magi.registerFormCondition();

                    if (event.getLevel().isClientSide) {
                        Avatar.inputModule.registerListeners();
                        Avatar.reloadFX();
                        System.out.println("InputModule Initiated!");
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void OnPlayerLeaveWorld(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            // TODO - Causes whole server to crash when player leaves
            //      java.lang.NullPointerException: Cannot invoke "com.amuzil.omegasource.magus.input.InputModule.getFormsTree()"
            //      because "com.amuzil.omegasource.magus.Magus.keyboardMouseInputModule" is null

            Magi magi = Magi.get((LivingEntity) event.getEntity());
            if (magi != null)
                magi.unregisterFormCondition();

        } else if (event.getEntity() instanceof Player && event.getLevel().isClientSide) {
            if (Avatar.inputModule != null) { // Temporary fix until we decide which side to make InputModules
                Avatar.inputModule.terminate();
            }
        }
    }

    @SubscribeEvent
    public static void worldTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() != null) {
            if (Magi.get(event.getEntity()) != null) {
//                if (event.getPhase().equals(TickEvent.Phase)) {
                Magi magi = Magi.get(event.getEntity());
                magi.onUpdate();
//                }
            }
        }
    }
}