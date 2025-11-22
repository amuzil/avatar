package com.amuzil.av3.gui;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.element.Element;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import static com.amuzil.av3.data.attachment.AvatarAttachments.ACTIVE_ELEMENT;
import static com.amuzil.av3.data.attachment.AvatarAttachments.IS_BENDING;

@EventBusSubscriber(
        modid = Avatar.MOD_ID,
        value = Dist.CLIENT
)
public class BendingHudOverlay {
    private static final ResourceLocation WATER_ICON =
            ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "textures/gui/sprites/elements/water_active.png");
    private static final ResourceLocation EARTH_ICON =
            ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "textures/gui/sprites/elements/earth_active.png");
    private static final ResourceLocation AIR_ICON =
            ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "textures/gui/sprites/elements/air_active.png");
    private static final ResourceLocation FIRE_ICON =
            ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "textures/gui/sprites/elements/fire_active.png");

    // Size in GUI pixels you want to render the icon at.
    private static final int ICON_SIZE = 32;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return;

        LocalPlayer player = mc.player;
        if (player == null) return;

        // --- Read current element from your data attachment (client copy) ---
        // Replace this with however you actually store it:
        boolean isBending = player.getData(IS_BENDING);
        Element selected = player.getData(ACTIVE_ELEMENT);

        if (!isBending || selected == null) return;

        ResourceLocation icon = getIconFor(selected);
        if (icon == null) return;

        GuiGraphics g = event.getGuiGraphics();

        // Bottom-left position (8px margin)
        int screenHeight = g.guiHeight();
        int x = 8;
        int y = screenHeight - ICON_SIZE - 8;

        // Draw the icon
        // (u, v, texWidth, texHeight) = 0,0,ICON_SIZE,ICON_SIZE since each PNG is just the icon.
        g.blit(
                icon,
                x, y,
                0.0F, 0.0F,
                ICON_SIZE, ICON_SIZE,
                ICON_SIZE, ICON_SIZE
        );
    }

    private static ResourceLocation getIconFor(Element type) {
        return switch (type.nickName()) {
            case "water" -> WATER_ICON;
            case "earth" -> EARTH_ICON;
            case "air"   -> AIR_ICON;
            case "fire"  -> FIRE_ICON;
            default -> null;
        };
    }
}
