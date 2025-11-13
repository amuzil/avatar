package com.amuzil.av3.input;

import com.amuzil.av3.bending.BendingSelection;
import com.amuzil.av3.bending.form.BendingForm;
import com.amuzil.av3.bending.form.BendingForms;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.network.AvatarNetwork;
import com.amuzil.av3.network.packets.form.ExecuteFormPacket;
import com.amuzil.av3.network.packets.form.ReleaseFormPacket;
import com.amuzil.magus.form.ActiveForm;
import com.amuzil.magus.registry.Registries;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.HashMap;
import java.util.function.Consumer;

import static com.amuzil.av3.data.capability.AvatarCapabilities.getOrCreateBender;
import static com.amuzil.av3.input.KeyBindings.*;


public class InputModule {
    private final Consumer<InputEvent.Key> keyboardListener;
    private final Consumer<InputEvent.MouseButton.Pre> mouseListener;
    private final Consumer<ClientTickEvent.Pre> tickEventConsumer;

    private boolean isSelecting = false;
    private boolean isHoldingShift = false;
    private boolean isHoldingCtrl = false;
    private boolean isHoldingAlt = false;
    private BendingForm currentForm = BendingForms.NULL;
    private boolean isBending = true;
    private BendingForm.Type.Motion motion = BendingForm.Type.Motion.NONE;
    private final long DOUBLE_TAP_THRESHOLD = 250; // milliseconds
    private final HashMap<BendingForm.Type.Motion, Long> lastPressedForm = new HashMap<>();
    private final HashMap<Integer, Integer> glfwKeysDown = new HashMap<>();
    private Bender bender;

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
                            case InputConstants.KEY_LSHIFT -> isHoldingShift = true;
                        }
                    }
                    case InputConstants.RELEASE -> {
                        switch (key) {
                            case InputConstants.KEY_LCONTROL -> isHoldingCtrl = false;
                            case InputConstants.KEY_LALT -> isHoldingAlt = false;
                            case InputConstants.KEY_LSHIFT -> isHoldingShift = false;
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
            if (Minecraft.getInstance().getConnection() != null &&
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

        DASH_KEY_MAPPINGS.forEach((direction, key) -> {
            if (key.isDown()) {
                int heldTicks = glfwKeysDown.getOrDefault(key.getKey().getValue(), 0);
                if (key.getKey().getValue() == InputConstants.KEY_SPACE)
                    glfwKeysDown.put(key.getKey().getValue(), heldTicks + 1);
                // Check == 1 to account for FORM_KEY_MAPPINGS using default MC keys (W, A, S, D, SPACE)
                if (heldTicks == 1)
                    checkDash(direction);
            } else {
                if (glfwKeysDown.containsKey(key.getKey().getValue())) {
                    releaseForm(BendingForms.STEP, key.getKey().getValue());
                }
            }
        });

        MOUSE_FORM_MAPPINGS.forEach((key, form) -> {
            if (glfwKeysDown.containsKey(key)) {
                int heldTicks = glfwKeysDown.getOrDefault(key, 0);
                glfwKeysDown.put(key, heldTicks + 1);
                if (heldTicks == 0)
                    checkForm(form);
            } else {
//                if (glfwKeysDown.containsKey(key)) {
//                    releaseForm(form, key);
//                }
            }
        });

        if (selectTargetKey.isDown() && !isSelecting) {
            isSelecting = true;
            handleSelectRaycast();
            bender.syncSelectionToServer();
//            sendDebugMsg("Selection: " + bender.getSelection().target);
        }
    }

    private void checkForm(BendingForm form) { // Check if the form met the conditions before sending the packet
        if (isBending) {
            if (!(isHoldingCtrl || isHoldingAlt)) {
                if (form.equals(BendingForms.STRIKE) || form.equals(BendingForms.BLOCK))
                    sendFormPacket(form, false);
            } else if (isHoldingCtrl && form.type().equals(BendingForm.Type.MOTION)) {
                sendFormPacket(form, false);
            } else if (isHoldingAlt && form.type().equals(BendingForm.Type.SHAPE)) {
                sendFormPacket(form, false);
            }
//            else if (form.type().equals(BendingForm.Type.INITIALIZER)) {
//                sendFormPacket(form, false);
//            }
        }
    }

    private void handleSelectRaycast() {
        if (isHoldingShift) {
            bender.getSelection().setSelf();
        } else {
            Minecraft mc = Minecraft.getInstance();
            double distance = 15; // TODO - Create Bender DataTrait
            assert mc.player != null;
            HitResult result = ProjectileUtil.getHitResultOnViewVector(mc.player, entity -> true, distance);
            switch (result.getType()) {
                case ENTITY -> trackEntityResult((EntityHitResult) result);
                case BLOCK -> trackBlockResult((BlockHitResult) result);
                case MISS -> handleMiss();
            }
        }
        isSelecting = false;
    }

    private void handleMiss() {
        // set selection type to none
        bender.getSelection().reset();
    }

    private void trackBlockResult(BlockHitResult result) {
        bender.getSelection().setBlockPos(result.getBlockPos());
    }

    private void trackEntityResult(EntityHitResult result) {
        BendingSelection selection = bender.getSelection();
        selection.addEntityId(result.getEntity().getUUID());
        bender.setSelection(selection);
    }

    private void releaseForm(BendingForm form, int key) {
        glfwKeysDown.remove(key);
        if (!form.type().equals(BendingForm.Type.INITIALIZER) && form.name().equals(currentForm.name())) {
            sendFormPacket(form, true);
        }
    }

    private void sendFormPacket(BendingForm form, boolean released) {
        ActiveForm activeForm = new ActiveForm(form, !released);
        activeForm.setDirection(motion); // TODO - Improve this impl, maybe create BendingContext class
        if (!released) {
            // send Form execute packet
            AvatarNetwork.sendToServer(new ExecuteFormPacket(activeForm.serializeNBT()));
            currentForm = form;
        } else {
            // send Form release packet
            AvatarNetwork.sendToServer(new ReleaseFormPacket(activeForm.serializeNBT()));
            currentForm = BendingForms.NULL;
        }
    }

    private void checkDash(BendingForm.Type.Motion dashDirection) {
        if (isDoubleTap(dashDirection) && !isHoldingCtrl && !isHoldingShift && !isHoldingAlt) {
            bender.setDeltaMovement(bender.getEntity().getDeltaMovement());
            bender.syncDeltaMovementToServer();
            sendFormPacket(BendingForms.STEP, false);
        }
    }

    private boolean isDoubleTap(BendingForm.Type.Motion direction) {
        long currentTime = System.currentTimeMillis();
        long lastTime = lastPressedForm.getOrDefault(direction, 0L);
        if (currentTime - lastTime < DOUBLE_TAP_THRESHOLD) {
            lastPressedForm.put(direction, 0L); // Reset to avoid triple tap
            motion = direction;
            return true;
        }
        lastPressedForm.put(direction, currentTime);
        motion = BendingForm.Type.Motion.NONE;
        return false;
    }

    public boolean keyPressed(int key) {
        return glfwKeysDown.containsKey(key);
    }

    public int keyPressedTicks(int key) {
        return glfwKeysDown.getOrDefault(key, 0);
    }

    public void registerListeners() {
        assert Minecraft.getInstance().player != null;
        bender = getOrCreateBender(Minecraft.getInstance().player);
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, InputEvent.Key.class, keyboardListener);
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, InputEvent.MouseButton.Pre.class, mouseListener);
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ClientTickEvent.Pre.class, tickEventConsumer);
    }

    public void unRegisterListeners() {
        NeoForge.EVENT_BUS.unregister(keyboardListener);
        NeoForge.EVENT_BUS.unregister(mouseListener);
        NeoForge.EVENT_BUS.unregister(tickEventConsumer);
    }

    public void terminate() {
        isHoldingShift = false;
        isHoldingCtrl = false;
        isHoldingAlt = false;
        unRegisterListeners();
        glfwKeysDown.clear();
        lastPressedForm.clear();
    }

    public void toggleListeners() {
        if (!isBending) {
            registerListeners();
            isBending = true;
            System.out.println("Enabled!");
            Player player = Minecraft.getInstance().player;
            assert player != null;
            Bender bender = getOrCreateBender(player);
            bender.printBenderData();
//            Registries.printAll();
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
