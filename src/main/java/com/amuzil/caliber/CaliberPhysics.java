package com.amuzil.caliber;

import com.amuzil.caliber.example.entity.CaliberEntities;
import com.amuzil.caliber.example.renderer.CaliberEntityRenderers;
import com.amuzil.caliber.physics.bullet.collision.space.generator.PressureGenerator;
import com.amuzil.caliber.physics.bullet.collision.space.generator.TerrainGenerator;
import com.amuzil.caliber.physics.bullet.natives.NativeLoader;
import com.amuzil.caliber.physics.event.ClientEventHandler;
import com.amuzil.caliber.physics.event.ServerEventHandler;
import com.amuzil.caliber.physics.network.CaliberNetwork;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CaliberPhysics {
    public static final String MOD_ID = "caliberps";
    public static final Logger LOGGER = LogManager.getLogger();

//    public CaliberPhysics(IEventBus modEventBus, ModContainer modContainer) {}

    public static void init(IEventBus modEventBus, ModContainer modContainer) {
        NativeLoader.load();
        java.util.logging.LogManager.getLogManager().reset(); // prevent annoying libbulletjme spam

        modEventBus.addListener(CaliberPhysics::setup);
        modEventBus.addListener(CaliberPhysics::setupClient);
        modEventBus.addListener(CaliberNetwork::register);
        modEventBus.addListener(CaliberEntityRenderers::registerEntityRenderers);

        CaliberEntities.register(modEventBus);
    }

    private static void setup(final FMLCommonSetupEvent event) {
        IEventBus bus = NeoForge.EVENT_BUS;
        bus.register(ServerEventHandler.class);
        bus.register(PressureGenerator.class);
        bus.register(TerrainGenerator.class);
    }

    private static void setupClient(final FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(ClientEventHandler.class);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static ResourceLocation id(Class<?> clazz) {
        return id(clazz.getSimpleName().toLowerCase());
    }
}