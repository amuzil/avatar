package com.amuzil.omegasource.server;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.condition.conditions.FormCondition;
import com.amuzil.omegasource.bending.form.ActiveForm;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import com.amuzil.omegasource.api.magus.skill.utils.capability.entity.Magi;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class ServerEvents {
    static FormCondition formCondition = new FormCondition();

    @SubscribeEvent
    public static void worldStart(LevelEvent event) {}

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return; // Ignore non-player entities
        }

        if (event.getLevel().isClientSide() && event.getEntity() instanceof Player player) {
            assert Minecraft.getInstance().player != null;
            if (Minecraft.getInstance().player.equals(player)) {
                Magi magi = Magi.get(player);
                if (magi != null) {
                    formCondition.register("formCondition", () -> {
                        ActiveForm activeForm = new ActiveForm(formCondition.form(), formCondition.active());
                        if (formCondition.active()) {
                            magi.coomplexForms.add(activeForm);
                        } else {
                            magi.coomplexForms.remove(activeForm);
                        }
                        Avatar.LOGGER.info("activeForms: {}", magi.coomplexForms);
                    }, () -> {});

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
            if (Avatar.inputModule != null) { // Temporary fix until we decide which side to make InputModules
                Avatar.inputModule.terminate();
                formCondition.unregister();
            }
        }
    }
}