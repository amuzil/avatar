package com.amuzil.omegasource.api.rayon.impl;

import com.amuzil.omegasource.api.rayon.impl.bullet.collision.space.generator.PressureGenerator;
import com.amuzil.omegasource.api.rayon.impl.bullet.collision.space.generator.TerrainGenerator;
import com.amuzil.omegasource.api.rayon.impl.bullet.natives.NativeLoader;
import com.amuzil.omegasource.api.rayon.impl.event.ClientEventHandler;
import com.amuzil.omegasource.api.rayon.impl.event.ServerEventHandler;
import com.amuzil.omegasource.api.rayon.impl.example.client.init.RayonExampleEntityRenderers;
import com.amuzil.omegasource.api.rayon.impl.example.init.RayonExampleEntities;
import com.amuzil.omegasource.api.rayon.impl.packet.RayonPacketHandlers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Rayon {
    public static final String MOD_ID = "carryon";
    public static final Logger LOGGER = LogManager.getLogger("CarryOn");

    public Rayon(FMLJavaModLoadingContext context) {
        NativeLoader.load();

        IEventBus modBus = context.getModEventBus();
        modBus.addListener(this::clientInit);
        modBus.addListener(this::commonInit);
        modBus.addListener(RayonExampleEntityRenderers::registerEntityRenderers);

        RayonExampleEntities.register(modBus);

        // prevent annoying libbulletjme spam
        java.util.logging.LogManager.getLogManager().reset();
    }
    
    private void clientInit(FMLClientSetupEvent event) {
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.register(ClientEventHandler.class);
        RayonPacketHandlers.registerPackets();
    }
    
    private void commonInit(FMLCommonSetupEvent event) {
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.register(ServerEventHandler.class);
        forgeBus.register(PressureGenerator.class);
        forgeBus.register(TerrainGenerator.class);
    }
    
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}