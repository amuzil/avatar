package com.amuzil.omegasource.input;

import com.amuzil.omegasource.bending.form.Form;
import com.amuzil.omegasource.bending.form.Forms;
import com.amuzil.omegasource.network.AvatarNetwork;
import com.amuzil.omegasource.network.packets.forms.ExecuteFormPacket;
import com.amuzil.omegasource.network.packets.forms.ReleaseFormPacket;
import com.amuzil.omegasource.api.magus.skill.utils.capability.entity.Magi;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;


public class InputModule {
    private final Consumer<InputEvent.Key> keyboardListener;
    private final Consumer<InputEvent.MouseButton> mouseListener;
    private final Consumer<TickEvent.ClientTickEvent> tickEventConsumer;

    private boolean isHoldingShift = false;
    private boolean isHoldingControl = false;
    private boolean isHoldingAlt = false;
    private Form currentForm = Forms.NULL;
    private boolean isBending = true;
    protected final List<Form> activeForms = Collections.synchronizedList(new LinkedList<>());
    private final HashMap<Integer, Integer> glfwKeysDown = new HashMap<>();

    public InputModule() {
        this.keyboardListener = keyboardEvent -> {
            int key = keyboardEvent.getKey();
            // NOTE: Minecraft's InputEvent.Key can only listen to the action InputConstants.REPEAT of one key at a time
            // tldr: it only fires the repeat event for the last key

            switch (keyboardEvent.getAction()) {
                case InputConstants.PRESS -> {
                    if (!keyPressed(key)) {
                        glfwKeysDown.put(key, 0);
                    }
                    switch (key) {
                        case InputConstants.KEY_LSHIFT -> isHoldingShift = true;
                        case InputConstants.KEY_LCONTROL -> isHoldingControl = true;
                        case InputConstants.KEY_LALT -> isHoldingAlt = true;
                    }
                }
                case InputConstants.RELEASE -> {
                    if (keyPressed(key)) {
                        glfwKeysDown.remove(key);
                        switch (key) {
                            case InputConstants.KEY_LSHIFT -> isHoldingShift = false;
                            case InputConstants.KEY_LCONTROL -> isHoldingControl = false;
                            case InputConstants.KEY_LALT -> isHoldingAlt = false;
                            default -> CheckFormsRelease(key);
                        }
                    }
                }
            }
        };

        this.mouseListener = mouseEvent -> {
            int key = mouseEvent.getButton();
            switch (mouseEvent.getAction()) {
                case InputConstants.PRESS -> {
                    if (!keyPressed(key)) {
                        glfwKeysDown.put(key, 0);
                    }
                }
                case InputConstants.RELEASE -> {
                    if (keyPressed(key)) {
                        glfwKeysDown.remove(key);
                        CheckFormsRelease(key);
                    }
                }
            }
        };

        this.tickEventConsumer = tickEvent -> {
            if (tickEvent.phase == TickEvent.ClientTickEvent.Phase.START && Minecraft.getInstance().getOverlay() == null) {
                glfwKeysDown.forEach((key, ticks) -> {
                    if (ticks == 0 && Minecraft.getInstance().getConnection() != null) {
                        CheckFormsExecute(key);
                    }
                    glfwKeysDown.put(key, ticks+1);
                });
            }
        };
    }

    private void CheckFormsExecute(int key) {
        if (isBending) {
            if (!(isHoldingShift || isHoldingAlt || isHoldingControl)) {
                switch (key) {
                    case InputConstants.MOUSE_BUTTON_LEFT -> ExecuteForm(Forms.STRIKE);
                    case InputConstants.MOUSE_BUTTON_RIGHT -> ExecuteForm(Forms.BLOCK);
                }
            } else if (isHoldingShift) {
                switch (key) {
                    case InputConstants.KEY_W -> ExecuteForm(Forms.PUSH);
                    case InputConstants.KEY_S -> ExecuteForm(Forms.PULL);
                    case InputConstants.KEY_A -> ExecuteForm(Forms.LEFT);
                    case InputConstants.KEY_D -> ExecuteForm(Forms.RIGHT);
                    case InputConstants.KEY_Q -> ExecuteForm(Forms.LOWER);
                    case InputConstants.KEY_E -> ExecuteForm(Forms.RAISE);
                    case InputConstants.KEY_R -> ExecuteForm(Forms.ROTATE);
                    case InputConstants.KEY_LALT -> ExecuteForm(Forms.ARC);
                }
            }
        }
    }

    private void CheckFormsRelease(int key) {
        switch (key) {
            case InputConstants.MOUSE_BUTTON_LEFT -> ReleaseForm(Forms.STRIKE);
            case InputConstants.MOUSE_BUTTON_RIGHT -> ReleaseForm(Forms.BLOCK);
            case InputConstants.KEY_W -> ReleaseForm(Forms.PUSH);
            case InputConstants.KEY_S -> ReleaseForm(Forms.PULL);
            case InputConstants.KEY_A -> ReleaseForm(Forms.LEFT);
            case InputConstants.KEY_D -> ReleaseForm(Forms.RIGHT);
            case InputConstants.KEY_Q -> ReleaseForm(Forms.LOWER);
            case InputConstants.KEY_E -> ReleaseForm(Forms.RAISE);
            case InputConstants.KEY_R -> ReleaseForm(Forms.ROTATE);
            case InputConstants.KEY_LALT -> ReleaseForm(Forms.ARC);
        }
    }

    private void ExecuteForm(Form form) {
        // send form execute packet
        AvatarNetwork.sendToServer(new ExecuteFormPacket(form));
        activeForms.add(form);
        // track current form executing
        currentForm = form;
    }

    private void ReleaseForm(Form form) {
        // send form release packet
        AvatarNetwork.sendToServer(new ReleaseFormPacket(form));
        activeForms.remove(form);
        // reset current form executing
        currentForm = Forms.NULL;
    }

    public boolean keyPressed(int key) {
        return glfwKeysDown.containsKey(key);
    }

    public Integer keyPressedTicks(int key) {
        return glfwKeysDown.getOrDefault(key, 0);
    }

    public void registerListeners() {
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
        unRegisterListeners();
        activeForms.clear();
        glfwKeysDown.clear();
//        Magi magi = Magi.get(Minecraft.getInstance().player);
//        if (magi != null)
//            magi.complexForms.clear();
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

    // Send a message to in-game chat
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
