package com.amuzil.omegasource.input;

import com.amuzil.omegasource.api.magus.capability.entity.Magi;
import com.amuzil.omegasource.api.magus.form.Form;
import com.amuzil.omegasource.bending.BendingForms;
import com.amuzil.omegasource.bending.BendingSelection;
import com.amuzil.omegasource.network.AvatarNetwork;
import com.amuzil.omegasource.network.packets.forms.ExecuteFormPacket;
import com.amuzil.omegasource.network.packets.forms.ReleaseFormPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.HashMap;
import java.util.function.Consumer;

import static com.amuzil.omegasource.bending.BendingSelection.Type.selectionTypes;
import static com.amuzil.omegasource.input.KeyBindings.*;


public class InputModule {
    private final Consumer<InputEvent.Key> keyboardListener;
    private final Consumer<InputEvent.MouseButton> mouseListener;
    private final Consumer<TickEvent.ClientTickEvent> tickEventConsumer;

    private boolean isHoldingShift = false;
    private boolean isHoldingCtrl = false;
    private boolean isHoldingAlt = false;
    private Form currentForm = BendingForms.NULL;
    private boolean isBending = true;
    private final HashMap<Integer, Integer> glfwKeysDown = new HashMap<>();
    private final long DOUBLE_TAP_THRESHOLD = 250; // milliseconds
    private final HashMap<Form, Long> lastPressedForm = new HashMap<>();
    private Magi magi;
    private BendingSelection.Type selection = BendingSelection.Type.NONE;

    public InputModule() {
        this.keyboardListener = keyboardEvent -> {
            int key = keyboardEvent.getKey();
            // NOTE: Minecraft's InputEvent.Key can only listen to the action InputConstants.REPEAT of one key at a time
            // tldr: it only fires the repeat event for the last key
            if (Minecraft.getInstance().screen == null) {
                switch (keyboardEvent.getAction()) {
                    case InputConstants.PRESS -> {
                        switch (key) {
                            case InputConstants.KEY_LCONTROL -> isHoldingCtrl = true;
                            case InputConstants.KEY_LALT -> isHoldingAlt = true;
//                            case InputConstants.KEY_LSHIFT -> isHoldingShift = true;
                        }
                    }
                    case InputConstants.RELEASE -> {
                        if (keyPressed(key)) {
                            switch (key) {
                                case InputConstants.KEY_LCONTROL -> isHoldingCtrl = false;
                                case InputConstants.KEY_LALT -> isHoldingAlt = false;
//                                case InputConstants.KEY_LSHIFT -> isHoldingShift = false;
                            }
                        }
                    }
                }
            }
        };

        this.mouseListener = mouseEvent -> {
            if (Minecraft.getInstance().screen == null)  {
                int key = mouseEvent.getButton();
                switch (mouseEvent.getAction()) {
                    case InputConstants.PRESS -> {
                        if (!keyPressed(key)) {
                            glfwKeysDown.put(key, 0);
                        }
                    }
                    case InputConstants.RELEASE -> {
                        if (keyPressed(key)) {
                            releaseForm(MOUSE_FORM_MAPPINGS.getOrDefault(key, BendingForms.NULL), key);
                        }
                    }
                }
            }
        };

        this.tickEventConsumer = tickEvent -> {
            if (tickEvent.phase == TickEvent.ClientTickEvent.Phase.START &&
                    Minecraft.getInstance().getConnection() != null &&
                    Minecraft.getInstance().getOverlay() == null &&
                    Minecraft.getInstance().screen == null) {
                checkInputs();
            }
        };
    }

    private void checkInputs() {
        FORM_KEY_MAPPINGS.forEach((form, key) -> {
            if (key.isDown()) {
                int heldTicks = glfwKeysDown.getOrDefault(key.getKey().getValue(), 0);
                glfwKeysDown.put(key.getKey().getValue(), heldTicks + 1);
                if (heldTicks == 0)
                    checkForm(form);
            } else {
                if (glfwKeysDown.containsKey(key.getKey().getValue())) {
                    releaseForm(form, key.getKey().getValue());
                }
            }
        });

        MOUSE_FORM_MAPPINGS.forEach((key, form) -> {
            if (glfwKeysDown.containsKey(key)) {
                int heldTicks = glfwKeysDown.getOrDefault(key, 0);
                glfwKeysDown.put(key, heldTicks + 1);
                if (heldTicks == 0)
                    checkForm(form);
            }
        });
    }

    private void checkForm(Form form) { // Check if the form met the conditions before sending the packet
        //      - Implement BendingForm class and link it to the Form class
        if (isBending) {
            if (!(isHoldingCtrl || isHoldingAlt)) {
                if (form.equals(BendingForms.TARGET)) { // Don't send target Form packet
                    int index = selection.ordinal() + 1;
                    if (index >= selectionTypes.length)
                        index = 0;
                    selection = selectionTypes[index];
                    sendDebugMsg("Current Selection: " + selection);
                } else if (form.equals(BendingForms.STRIKE) || form.equals(BendingForms.BLOCK)) {
                    sendFormPacket(form, false);
                } else if (isDoubleTap(form)) {
                    sendFormPacket(BendingForms.STEP, false);
                }
            } else if (isHoldingCtrl && form.type().equals(Form.Type.MOTION)) {
                sendFormPacket(form, false);
            } else if (isHoldingAlt && form.type().equals(Form.Type.SHAPE)) {
                sendFormPacket(form, false);
            } else if (form.type().equals(Form.Type.INITIALIZER)) {
                sendFormPacket(form, false);
            }
        }
    }

    private void releaseForm(Form form, int key) {
        glfwKeysDown.remove(key);
        sendFormPacket(form, true);
    }

    private void sendFormPacket(Form form, boolean released) {
        if (!released) {
            // send Form execute packet
            AvatarNetwork.sendToServer(new ExecuteFormPacket(form));
            currentForm = form;
        } else {
            // send Form release packet
            AvatarNetwork.sendToServer(new ReleaseFormPacket(form));
            currentForm = BendingForms.NULL;
        }
    }

    public boolean isDoubleTap(Form form) {
        if (form.type().equals(Form.Type.MOTION)) {
            long currentTime = System.currentTimeMillis();
            long lastTime = lastPressedForm.getOrDefault(form, 0L);

            if (currentTime - lastTime < DOUBLE_TAP_THRESHOLD) {
                lastPressedForm.put(form, 0L); // Reset to avoid triple tap
                return true;
            }

            lastPressedForm.put(form, currentTime);
        }
        return false;
    }

    public boolean keyPressed(int key) {
        return glfwKeysDown.containsKey(key);
    }

    public int keyPressedTicks(int key) {
        return glfwKeysDown.getOrDefault(key, 0);
    }

    public void registerListeners() {
        magi = Magi.get(Minecraft.getInstance().player);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, InputEvent.Key.class, keyboardListener);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, InputEvent.MouseButton.class, mouseListener);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.ClientTickEvent.class, tickEventConsumer);
    }

    public void unRegisterListeners() {
        MinecraftForge.EVENT_BUS.unregister(keyboardListener);
        MinecraftForge.EVENT_BUS.unregister(mouseListener);
        MinecraftForge.EVENT_BUS.unregister(tickEventConsumer);
    }

    public void terminate() {
        isHoldingShift = false;
        isHoldingCtrl = false;
        isHoldingAlt = false;
        unRegisterListeners();
        glfwKeysDown.clear();
        magi = Magi.get(Minecraft.getInstance().player);
        if (magi != null) {
            magi.formPath.clearAll();
        }
    }

    public void toggleListeners() {
        if (!isBending) {
            registerListeners();
            isBending = true;
            System.out.println("Enabled!");
        } else {
            terminate();
            isBending = false;
            System.out.println("Disabled!");
        }
    }

    // Send message to in-game chat
    public static void sendDebugMsg(String msg) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.execute(() -> {
            LocalPlayer player = minecraft.player;
            if (player != null) {
                Component text = Component.literal(msg);
                player.sendSystemMessage(text);
            } else {
                System.err.println("sendDebugMsg failed: player is null");
            }
        });
    }
}
