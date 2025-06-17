package com.amuzil.omegasource;

import com.amuzil.omegasource.entity.AvatarEntities;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import com.amuzil.omegasource.entity.modules.collision.FireModule;
import com.amuzil.omegasource.entity.modules.collision.SimpleDamageModule;
import com.amuzil.omegasource.entity.modules.collision.SimpleKnockbackModule;
import com.amuzil.omegasource.entity.modules.entity.GrowModule;
import com.amuzil.omegasource.entity.modules.entity.TimeoutModule;
import com.amuzil.omegasource.entity.modules.force.BindModule;
import com.amuzil.omegasource.entity.modules.force.ChangeSpeedModule;
import com.amuzil.omegasource.entity.modules.force.MoveModule;
import com.amuzil.omegasource.entity.modules.render.PhotonModule;
import com.amuzil.omegasource.input.InputModule;
import com.amuzil.omegasource.network.AvatarNetwork;
import com.amuzil.omegasource.api.magus.registry.Registries;
import com.amuzil.omegasource.utils.commands.AvatarCommands;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
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
    public static InputModule inputModule;

    public static FX fire_bloom;
    public static FX fire_bloom_perma;
    public static FX air_perma;
    public static FX blue_fire;
    public static FX blue_fire_perma;
    public static FX orb_bloom;
    public static FX water;
    public static FX steam;

    public Avatar(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        // Register the setup method for mod loading
        modEventBus.addListener(this::setup);
        // Register the enqueueIMC method for mod loading
        modEventBus.addListener(this::enqueueIMC);
        // Register the processIMC method for mod loading
        modEventBus.addListener(this::processIMC);
        // Register the setupClient method for mod loading
        modEventBus.addListener(this::setupClient);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register Entities
        AvatarEntities.register(modEventBus);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some pre init code
        Registries.init();
        AvatarNetwork.register();

        ModuleRegistry.register("Move", MoveModule::new);
        ModuleRegistry.register("Timeout", TimeoutModule::new);
        ModuleRegistry.register("Grow", GrowModule::new);
        ModuleRegistry.register("SimpleKnockback", SimpleKnockbackModule::new);
        ModuleRegistry.register("FireTime", FireModule::new);
        ModuleRegistry.register("SimpleDamage", SimpleDamageModule::new);
        ModuleRegistry.register("ChangeSpeed", ChangeSpeedModule::new);
        ModuleRegistry.register("Photon", PhotonModule::new);
        ModuleRegistry.register("Bind", BindModule::new);
    }

    private void setupClient(final FMLClientSetupEvent event) {
        // Register the input modules
        inputModule = new InputModule();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {}

    private void processIMC(final InterModProcessEvent event) {}

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("Setting up Avatar commands...");
        AvatarCommands.register(event.getServer().getCommands().getDispatcher());
    }

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

            EntityRenderers.register(AvatarEntities.AVATAR_PROJECTILE_ENTITY_TYPE.get(), ThrownItemRenderer::new);
            EntityRenderers.register(AvatarEntities.AVATAR_BIND_ENTITY_TYPE.get(), ThrownItemRenderer::new);
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

//            PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
//                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "animation"),
//                    42, Avatar::registerPlayerAnimation);
        }
    }

    public static void reloadFX() {
        fire_bloom = FXHelper.getFX(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "fire_bloom"));
        fire_bloom_perma = FXHelper.getFX(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "fires_bloom_perma5"));
        air_perma = FXHelper.getFX(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "airs_perma8"));
        blue_fire = FXHelper.getFX(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "blue_fire"));
        blue_fire_perma = FXHelper.getFX(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "fire_bloom_perma5"));
        orb_bloom = FXHelper.getFX(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "orb_bloom"));
        water = FXHelper.getFX(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "water"));
        steam = FXHelper.getFX(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "steam"));
    }

    public static String isClientOrServer(boolean isClient) {
        return isClient ? "Client-Side" : "Server-Side";
    }

//    private static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
//        // This will be invoked for every new player
//        return new ModifierLayer<>();
//    }
}
