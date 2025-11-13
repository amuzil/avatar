package com.amuzil.caliber.physics.bullet.thread.util;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;

public class ClientUtil {
    public static boolean isClient() {
        return FMLLoader.getDist() == Dist.CLIENT;
    }

    public static boolean isPaused() {
        if (isClient())
            return Minecraft.getInstance().isPaused();
        return false;
    }

    public static boolean isConnectedToServer() {
        if (isClient())
            return Minecraft.getInstance().getConnection() != null;
        return false;
    }
}
