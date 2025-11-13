package com.amuzil.caliber;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CaliberPhysics {
    public static final String MOD_ID = "caliberps";
    public static final Logger LOGGER = LogManager.getLogger();

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static ResourceLocation id(Class<?> clazz) {
        return id(clazz.getSimpleName().toLowerCase());
    }
}