package com.amuzil.omegasource.input;

import com.amuzil.omegasource.Avatar;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;


@Mod.EventBusSubscriber(modid = Avatar.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KeyBindings {
    public static final KeyMapping keyToggleTree = new KeyMapping("key.keyboard.grave.accent", InputConstants.KEY_GRAVE, "key.categories.gameplay");;

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(keyToggleTree);
    }

    @SubscribeEvent
    public static void keyBindPress(InputEvent.Key press) {
        if (press.getKey() == keyToggleTree.getKey().getValue()) {
            if (press.getAction() == GLFW.GLFW_RELEASE) {
//                Magus.keyboardMouseInputModule.toggleListeners();
                Avatar.inputModule.toggleListeners();
                Avatar.reloadFX();
            }
        }
    }

}
