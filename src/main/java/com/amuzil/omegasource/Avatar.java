package com.amuzil.omegasource;

import com.amuzil.omegasource.api.magus.skill.Skills;
import com.amuzil.omegasource.api.magus.skill.utils.capability.CapabilityHandler;
import com.amuzil.omegasource.entity.AvatarEntities;
import com.amuzil.omegasource.input.DefaultInputModule;
import com.amuzil.omegasource.network.AvatarNetwork;
import com.amuzil.omegasource.registry.Registries;
import com.amuzil.omegasource.utils.AvatarCommand;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(Avatar.MOD_ID)
public class Avatar {
    // MOD ID reference
    public static final String MOD_ID = "av3";
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public static DefaultInputModule inputModule;

    public static FX fire_bloom = FXHelper.getFX(new ResourceLocation(Avatar.MOD_ID, "fire_bloom"));
    public static FX fire_bloom_perma = FXHelper.getFX(new ResourceLocation(Avatar.MOD_ID, "fire_bloom_perma"));
    public static FX blue_fire = FXHelper.getFX(new ResourceLocation(Avatar.MOD_ID, "blue_fire"));
    public static FX blue_fire_perma = FXHelper.getFX(new ResourceLocation(Avatar.MOD_ID, "blue_fire_perma"));
    public static FX orb_bloom = FXHelper.getFX(new ResourceLocation(Avatar.MOD_ID, "orb_bloom"));
    public static FX water = FXHelper.getFX(new ResourceLocation(Avatar.MOD_ID, "water"));
    public static FX steam = FXHelper.getFX(new ResourceLocation(Avatar.MOD_ID, "steam"));

    public Avatar() {
        // Register capabilities
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CapabilityHandler::registerCapabilities);
        // attach capabilities
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, CapabilityHandler::attachEntityCapability);
        // Register the setup method for mod loading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for mod loading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for mod loading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for mod loading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register Testing Entities
        // NOTE: This is strictly for testing and to be deleted later
        AvatarEntities.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some pre init code
        Registries.init();
        CapabilityHandler.init();
        AvatarNetwork.register();
        Skills.register();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        // Register the input modules
        inputModule = new DefaultInputModule();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {}

    private void processIMC(final InterModProcessEvent event) {}

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("Setting up Avatar commands...");
        AvatarCommand.register(event.getServer().getCommands().getDispatcher());
    }

    //Copied for 1.19
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            EntityRenderers.register(AvatarEntities.AIR_PROJECTILE_ENTITY_TYPE.get(), ThrownItemRenderer::new);
            EntityRenderers.register(AvatarEntities.WATER_PROJECTILE_ENTITY_TYPE.get(), ThrownItemRenderer::new);
            EntityRenderers.register(AvatarEntities.EARTH_PROJECTILE_ENTITY_TYPE.get(), ThrownItemRenderer::new);
            EntityRenderers.register(AvatarEntities.FIRE_PROJECTILE_ENTITY_TYPE.get(), ThrownItemRenderer::new);
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    public static void reloadFX() {
        fire_bloom = FXHelper.getFX(new ResourceLocation(Avatar.MOD_ID, "fire_bloom"));
        fire_bloom_perma = FXHelper.getFX(new ResourceLocation(Avatar.MOD_ID, "fire_bloom_perma"));
        blue_fire = FXHelper.getFX(new ResourceLocation(Avatar.MOD_ID, "blue_fire"));
        blue_fire_perma = FXHelper.getFX(new ResourceLocation(Avatar.MOD_ID, "blue_fire_perma"));
        orb_bloom = FXHelper.getFX(new ResourceLocation(Avatar.MOD_ID, "orb_bloom"));
        water = FXHelper.getFX(new ResourceLocation(Avatar.MOD_ID, "water"));
        steam = FXHelper.getFX(new ResourceLocation(Avatar.MOD_ID, "steam"));
    }
}
