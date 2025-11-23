package com.amuzil.av3.gui;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.element.Element;
import com.amuzil.av3.bending.element.Elements;
import com.amuzil.av3.network.AvatarNetwork;
import com.amuzil.av3.network.packets.bending.ChooseElementPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Random;

public class ElementSelectScreen extends Screen {

    // Sprite locations are *sprite* paths (textures/gui/sprites/<path>.png)
    private static final ResourceLocation FIRE_SPRITE = ResourceLocation.fromNamespaceAndPath(
            Avatar.MOD_ID, "elements/fire");
    private static final ResourceLocation FIRE_SPRITE_ACTIVE = ResourceLocation.fromNamespaceAndPath(
            Avatar.MOD_ID, "elements/fire_active");
    private static final ResourceLocation WATER_SPRITE = ResourceLocation.fromNamespaceAndPath(
            Avatar.MOD_ID, "elements/water");
    private static final ResourceLocation WATER_SPRITE_ACTIVE = ResourceLocation.fromNamespaceAndPath(
            Avatar.MOD_ID, "elements/water_active");
    private static final ResourceLocation EARTH_SPRITE = ResourceLocation.fromNamespaceAndPath(
            Avatar.MOD_ID, "elements/earth");
    private static final ResourceLocation EARTH_SPRITE_ACTIVE = ResourceLocation.fromNamespaceAndPath(
            Avatar.MOD_ID, "elements/earth_active");
    private static final ResourceLocation AIR_SPRITE = ResourceLocation.fromNamespaceAndPath(
            Avatar.MOD_ID, "elements/air");
    private static final ResourceLocation AIR_SPRITE_ACTIVE = ResourceLocation.fromNamespaceAndPath(
            Avatar.MOD_ID, "elements/air_active");

    /*
     * WidgetSprites: (enabled, disabled, enabledFocused, disabledFocused).
     */
    private static final WidgetSprites FIRE_BUTTON_SPRITES =
            new WidgetSprites(FIRE_SPRITE, FIRE_SPRITE, FIRE_SPRITE_ACTIVE, FIRE_SPRITE);
    private static final WidgetSprites WATER_BUTTON_SPRITES =
            new WidgetSprites(WATER_SPRITE, WATER_SPRITE, WATER_SPRITE_ACTIVE, WATER_SPRITE);
    private static final WidgetSprites EARTH_BUTTON_SPRITES =
            new WidgetSprites(EARTH_SPRITE, EARTH_SPRITE, EARTH_SPRITE_ACTIVE, EARTH_SPRITE);
    private static final WidgetSprites AIR_BUTTON_SPRITES =
            new WidgetSprites(AIR_SPRITE, AIR_SPRITE, AIR_SPRITE_ACTIVE, AIR_SPRITE);

    // Order requested: AIR → WATER → EARTH → FIRE (loop)
    private static final ResourceLocation[] RANDOM_FRAMES = new ResourceLocation[]{
            AIR_SPRITE,
            WATER_SPRITE,
            EARTH_SPRITE,
            FIRE_SPRITE
    };

    private static final ResourceLocation[] RANDOM_FRAMES_ACTIVE = new ResourceLocation[]{
            AIR_SPRITE_ACTIVE,
            WATER_SPRITE_ACTIVE,
            EARTH_SPRITE_ACTIVE,
            FIRE_SPRITE_ACTIVE
    };

    private static final Random RNG = new Random();

    private Element selectedElement;

    public ElementSelectScreen() {
        super(Component.translatable("screen.av3.element_select"));
    }

    @Override
    protected void init() {
        super.init();

        int buttonWidth = 32;
        int buttonHeight = 32;

        int spacing = 8;
        int totalWidth = buttonWidth * 4 + 3 * spacing;
        int startX = (this.width - totalWidth) / 2;
        int y = this.height / 2 - buttonHeight / 2;

        // FIRE
        this.addRenderableWidget(new ImageButton(
                startX,
                y,
                buttonWidth,
                buttonHeight,
                FIRE_BUTTON_SPRITES,
                btn -> onElementClicked(Elements.FIRE)
        ));

        // WATER
        this.addRenderableWidget(new ImageButton(
                startX + (buttonWidth + spacing),
                y,
                buttonWidth,
                buttonHeight,
                WATER_BUTTON_SPRITES,
                btn -> onElementClicked(Elements.WATER)
        ));

        // EARTH
        this.addRenderableWidget(new ImageButton(
                startX + 2 * (buttonWidth + spacing),
                y,
                buttonWidth,
                buttonHeight,
                EARTH_BUTTON_SPRITES,
                btn -> onElementClicked(Elements.EARTH)
        ));

        // AIR
        this.addRenderableWidget(new ImageButton(
                startX + 3 * (buttonWidth + spacing),
                y,
                buttonWidth,
                buttonHeight,
                AIR_BUTTON_SPRITES,
                btn -> onElementClicked(Elements.AIR)
        ));

        // RANDOM (5th button centered below the 4, with cycling icon)
        int randomY = y + buttonHeight + spacing;
        int randomX = this.width / 2 - buttonWidth / 2;

        this.addRenderableWidget(new RandomElementButton(
                randomX,
                randomY,
                buttonWidth,
                buttonHeight,
                btn -> onRandomElementClicked()
        ));
    }

    private void onElementClicked(Element element) {
        this.selectedElement = element;

        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(null);
        if (mc.player != null) {
            AvatarNetwork.sendToServer(
                    new ChooseElementPacket(mc.player.getUUID(), element.getId().toString())
            );
        }
    }

    private void onRandomElementClicked() {
        Element random = Elements.random();
        onElementClicked(random);
    }

    public Element getSelectedElement() {
        return selectedElement;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(
                this.font,
                this.title,
                this.width / 2,
                this.height / 2 - 40,
                0xFFFFFF
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /**
     * Image button whose icon cycles AIR → WATER → EARTH → FIRE in order.
     */
    private static class RandomElementButton extends ImageButton {
        private int frameIndex = 0;
        private int tickCounter = 0;

        public RandomElementButton(int x, int y, int width, int height, OnPress onPress) {
            // WidgetSprites are unused visually; we override renderWidget.
            super(x, y, width, height, FIRE_BUTTON_SPRITES, onPress);
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            // Advance animation every few frames
            tickCounter++;
            if (tickCounter % 10 == 0) { // change speed by adjusting the modulus
                frameIndex = (frameIndex + 1) % RANDOM_FRAMES.length;
            }

            ResourceLocation icon = this.isHoveredOrFocused()
                    ? RANDOM_FRAMES_ACTIVE[frameIndex]
                    : RANDOM_FRAMES[frameIndex];

            graphics.blitSprite(icon, this.getX(), this.getY(), this.width, this.height);
        }
    }
}
