package com.amuzil.av3.gui;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.element.Element;
import com.amuzil.av3.bending.element.Elements;
import com.amuzil.av3.network.AvatarNetwork;
import com.amuzil.av3.network.packets.bender.ChooseElementPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

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

    private Element selectedElement;

    public ElementSelectScreen() {
        super(Component.translatable("screen.av3.element_select"));
    }

    @Override
    protected void init() {
        super.init();

        // Button size (match your sprite or scale as desired)
        int buttonWidth = 32;
        int buttonHeight = 32;

        // Horizontal layout: 4 buttons centered on screen
        int totalWidth = buttonWidth * 4 + 3 * 8; // 8 px spacing
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
                startX + (buttonWidth + 8),
                y,
                buttonWidth,
                buttonHeight,
                WATER_BUTTON_SPRITES,
                btn -> onElementClicked(Elements.WATER)
        ));

        // EARTH
        this.addRenderableWidget(new ImageButton(
                startX + 2 * (buttonWidth + 8),
                y,
                buttonWidth,
                buttonHeight,
                EARTH_BUTTON_SPRITES,
                btn -> onElementClicked(Elements.EARTH)
        ));

        // AIR
        this.addRenderableWidget(new ImageButton(
                startX + 3 * (buttonWidth + 8),
                y,
                buttonWidth,
                buttonHeight,
                AIR_BUTTON_SPRITES,
                btn -> onElementClicked(Elements.AIR)
        ));
    }

    private void onElementClicked(Element element) {
        this.selectedElement = element;

        Minecraft.getInstance().setScreen(null);
        AvatarNetwork.sendToServer(new ChooseElementPacket(Minecraft.getInstance().player.getUUID(), element.getId().toString()));
    }

    public Element getSelectedElement() {
        return selectedElement;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Dark background behind GUI
        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        super.render(graphics, mouseX, mouseY, partialTick);

        // Draw title above buttons
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
        // If you want the game to keep running while this is open:
        return false;
    }
}
