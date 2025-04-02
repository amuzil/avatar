package com.amuzil.omegasource.server;

import com.amuzil.omegasource.Avatar;
import net.minecraft.server.level.ServerPlayer;
import com.amuzil.omegasource.api.magus.capability.entity.Magi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class ServerEvents {

    @SubscribeEvent
    public static void worldStart(LevelEvent event) {}


    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return; // Ignore non-player entities
        }

        if (event.getEntity() instanceof Player player) {
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
                Magi magi = Magi.get(event.getEntity());
                magi.onUpdate();
            }
        }
    }
}