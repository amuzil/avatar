package com.amuzil.omegasource.input;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.Form;
import com.amuzil.omegasource.bending.BendingForms;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;


@Mod.EventBusSubscriber(modid = Avatar.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyBindings {
    private static final HashMap<Form, KeyMapping> FORM_KEY_MAPPINGS = new HashMap<>();
    public static final HashMap<Integer, Form> FORM_KEYS = new HashMap<>();
    public static final KeyMapping toggleBendingKey = new KeyMapping("key.av3.bending_toggle", InputConstants.KEY_GRAVE, "key.categories.av3");

    static {
        // Initialize KeyMappings for each Form
        createKeyMapping(BendingForms.PUSH, InputConstants.KEY_W);
        createKeyMapping(BendingForms.PULL, InputConstants.KEY_S);
        createKeyMapping(BendingForms.RAISE, InputConstants.KEY_E);
        createKeyMapping(BendingForms.LOWER, InputConstants.KEY_Q);
        createKeyMapping(BendingForms.LEFT, InputConstants.KEY_A);
        createKeyMapping(BendingForms.RIGHT, InputConstants.KEY_D);
        createKeyMapping(BendingForms.ROTATE, InputConstants.KEY_R);
        createKeyMapping(BendingForms.EXPAND, InputConstants.KEY_X);
        createKeyMapping(BendingForms.COMPRESS, InputConstants.KEY_C);
        createKeyMapping(BendingForms.SPLIT, InputConstants.KEY_V);
        createKeyMapping(BendingForms.COMBINE, InputConstants.KEY_B);
        createKeyMapping(BendingForms.ARC, InputConstants.KEY_LCONTROL);
        createKeyMapping(BendingForms.SHAPE, InputConstants.KEY_LSHIFT);
        createKeyMapping(BendingForms.PHASE, InputConstants.KEY_LALT);
        createKeyMapping(BendingForms.STRIKE, InputConstants.MOUSE_BUTTON_LEFT);
        createKeyMapping(BendingForms.BLOCK, InputConstants.MOUSE_BUTTON_RIGHT);
        // Add more mappings as needed
    }

    private static void createKeyMapping(Form form, int defaultKey) {
        FORM_KEYS.put(defaultKey, form);
        FORM_KEY_MAPPINGS.put(form, new KeyMapping(
                String.format("key.av3.form.%s", form.name().toLowerCase()),
                defaultKey,
                "key.categories.av3"
        ));
    }

    public static KeyMapping getKeyMapping(Form form) {
        return FORM_KEY_MAPPINGS.getOrDefault(form, null);
    }

    public static Form getFormFromKey(int key) {
        return FORM_KEYS.getOrDefault(key, BendingForms.NULL);
    }

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        // Register all KeyMappings
        event.register(toggleBendingKey);
        FORM_KEY_MAPPINGS.values().forEach(event::register);
    }

    @Mod.EventBusSubscriber(modid = Avatar.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ModKeyInputHandler {
        @SubscribeEvent
        public static void keyPress(InputEvent.Key key) {
            if (Minecraft.getInstance().screen != null) return; // Ignore input when in GUI
            if (key.getKey() == toggleBendingKey.getKey().getValue()) {
                if (key.getAction() == InputConstants.RELEASE) {
                    Avatar.inputModule.toggleListeners();
                    Avatar.reloadFX();
                }
            }
        }
    }
}
