package com.amuzil.omegasource.input;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.bending.BendingForm;
import com.amuzil.omegasource.bending.BendingForms;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;


@Mod.EventBusSubscriber(modid = Avatar.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyBindings {
    static final HashMap<Integer, BendingForm> MOUSE_FORM_MAPPINGS = new HashMap<>();
    static final HashMap<BendingForm, KeyMapping> FORM_KEY_MAPPINGS = new HashMap<>();
    private static final KeyMapping toggleBendingKey = new KeyMapping(
            "key.av3.bending_toggle",
            InputConstants.KEY_GRAVE,
            "key.categories.av3");
    static final KeyMapping selectTargetKey = new KeyMapping(
            "key.av3.bending_toggle",
            InputConstants.KEY_TAB,
            "key.categories.av3");

    static {
        // Initialize KeyMappings for each Form
        createKeyMapping(BendingForms.STRIKE, InputConstants.MOUSE_BUTTON_LEFT);
        createKeyMapping(BendingForms.BLOCK, InputConstants.MOUSE_BUTTON_RIGHT);
        createKeyMapping(BendingForms.PUSH, InputConstants.KEY_W);
        createKeyMapping(BendingForms.PULL, InputConstants.KEY_S);
        createKeyMapping(BendingForms.LEFT, InputConstants.KEY_A);
        createKeyMapping(BendingForms.RIGHT, InputConstants.KEY_D);
        createKeyMapping(BendingForms.RAISE, InputConstants.KEY_F);
        createKeyMapping(BendingForms.LOWER, InputConstants.KEY_G);
        createKeyMapping(BendingForms.ROTATE, InputConstants.KEY_R);
        createKeyMapping(BendingForms.EXPAND, InputConstants.KEY_X);
        createKeyMapping(BendingForms.COMPRESS, InputConstants.KEY_C);
        createKeyMapping(BendingForms.SPLIT, InputConstants.KEY_V);
        createKeyMapping(BendingForms.COMBINE, InputConstants.KEY_B);
        createKeyMapping(BendingForms.ARC, InputConstants.KEY_LCONTROL);
        createKeyMapping(BendingForms.SHAPE, InputConstants.KEY_LALT);
        // Add more mappings as needed
    }

    private static void createKeyMapping(BendingForm form, int defaultKey) {
        if (defaultKey > 2) { // Exclude mouse buttons to avoid error
            KeyMapping keyMapping = new KeyMapping(
                    String.format("key.av3.form.%s", form.name().toLowerCase()),
                    defaultKey,
                    "key.categories.av3");
            FORM_KEY_MAPPINGS.put(form, keyMapping);
        } else {
            MOUSE_FORM_MAPPINGS.put(defaultKey, form);
        }
    }

    public static KeyMapping getKeyMapping(BendingForm form) {
        return FORM_KEY_MAPPINGS.getOrDefault(form, null);
    }

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        // Register all KeyMappings
        event.register(toggleBendingKey);
        event.register(selectTargetKey);
        FORM_KEY_MAPPINGS.values().forEach(event::register);
    }

    @Mod.EventBusSubscriber(modid = Avatar.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ModKeyInputHandler {
        @SubscribeEvent
        public static void keyPress(InputEvent.Key key) {
            if (Minecraft.getInstance().screen != null) return; // Ignore input when in GUI
            if (key.getKey() == toggleBendingKey.getKey().getValue()
                && key.getAction() == InputConstants.RELEASE) {

                Avatar.inputModule.toggleListeners();
                Avatar.reloadFX();
            }
//            if (key.getKey() == selectTargetKey.getKey().getValue()
//                && key.getAction() == InputConstants.RELEASE) {
//                handleSelectRaycast();
//            }
        }
    }
}
