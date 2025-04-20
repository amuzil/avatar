package com.amuzil.omegasource.utils.server;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.capability.CapabilityHandler;
import com.amuzil.omegasource.api.magus.capability.entity.Data;
import com.amuzil.omegasource.capability.AvatarCapabilities;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.capability.IBender;
import com.amuzil.omegasource.network.AvatarNetwork;
import com.amuzil.omegasource.network.packets.client.SyncBenderPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import com.amuzil.omegasource.api.magus.capability.entity.Magi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Player)) return; // Ignore non-player entities

        if (event.getEntity() instanceof Player player) {
            Bender bender = (Bender) Bender.getBender((LivingEntity) event.getEntity());
            bender.registerFormCondition();
            if (event.getLevel().isClientSide) {
                Avatar.inputModule.registerListeners();
                Avatar.reloadFX();
                System.out.println("InputModule Initiated!");
            }
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            // TODO - Causes whole server to crash when player leaves
            //      java.lang.NullPointerException: Cannot invoke "com.amuzil.omegasource.magus.input.InputModule.getFormsTree()"
            //      because "com.amuzil.omegasource.magus.Magus.keyboardMouseInputModule" is null

            Bender bender = (Bender) Bender.getBender((LivingEntity) event.getEntity());
            bender.unregisterFormCondition();

        } else if (event.getEntity() instanceof Player && event.getLevel().isClientSide) {
            if (Avatar.inputModule != null) { // Temporary fix until we decide which side to make InputModules
                Avatar.inputModule.terminate();
            }
        }
    }

    @SubscribeEvent
    public static void worldTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() != null && event.getEntity().isAlive()) {
            Bender bender = (Bender) Bender.getBender(event.getEntity());
            if (bender == null) return;
            bender.onUpdate();
        }
    }
}