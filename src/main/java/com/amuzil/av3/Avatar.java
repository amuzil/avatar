package com.amuzil.av3;

import com.amuzil.av3.data.BenderCache;
import com.amuzil.av3.data.attachment.AvatarAttachments;
import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.client.PhotonModule;
import com.amuzil.av3.entity.api.modules.client.SoundModule;
import com.amuzil.av3.entity.api.modules.collision.*;
import com.amuzil.av3.entity.api.modules.entity.GrowModule;
import com.amuzil.av3.entity.api.modules.entity.TimeoutModule;
import com.amuzil.av3.entity.api.modules.force.*;
import com.amuzil.av3.input.InputModule;
import com.amuzil.av3.network.AvatarNetwork;
import com.amuzil.av3.utils.sound.AvatarSounds;
import com.amuzil.caliber.CaliberPhysics;
import com.amuzil.magus.registry.Registries;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(Avatar.MOD_ID)
public class Avatar {
    public static final String MOD_ID = "av3";
    public static final Logger LOGGER = LogManager.getLogger();
    public static InputModule INPUT_MODULE;
    public static BenderCache BENDER_CACHE;

    public Avatar(IEventBus modEventBus, ModContainer modContainer) {
        CaliberPhysics.init(modEventBus, modContainer);

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::setupClient);
        modEventBus.addListener(AvatarNetwork::register);

        AvatarAttachments.register(modEventBus);
        AvatarEntities.register(modEventBus);
        AvatarSounds.register(modEventBus);

        Registries.registerForms();
        Registries.FORMS_REGISTER.register(modEventBus);
        Registries.SKILL_REGISTER.register(modEventBus);
        Registries.SKILL_CATEGORY_REGISTER.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        // NeoForge.EVENT_BUS.register(this);
        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        // modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void setup(final FMLCommonSetupEvent event) {

        ModuleRegistry.register(MoveModule::new);
        ModuleRegistry.register(CurveModule::new);
        ModuleRegistry.register(ControlModule::new);
        ModuleRegistry.register(BindModule::new);
        ModuleRegistry.register(OrbitModule::new);
        ModuleRegistry.register(ChangeSpeedModule::new);
        ModuleRegistry.register(TimeoutModule::new);
        ModuleRegistry.register(GrowModule::new);
        ModuleRegistry.register(FireModule::new);
        ModuleRegistry.register(SimpleDamageModule::new);
        ModuleRegistry.register(SimpleKnockbackModule::new);
        ModuleRegistry.register(AirCollisionModule::new);
        ModuleRegistry.register(WaterCollisionModule::new);
        ModuleRegistry.register(FireCollisionModule::new);
        ModuleRegistry.register(SoundModule::new);
        ModuleRegistry.register(PhotonModule::new);
    }

    private void setupClient(final FMLClientSetupEvent event) {
        LOGGER.info("Setting up Avatar Mod client-side...");
        LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        // Initialize the input modules
        INPUT_MODULE = new InputModule();
    }

//    private void enqueueIMC(final InterModEnqueueEvent event) {}

//    private void processIMC(final InterModProcessEvent event) {}

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static ResourceLocation id(Class<?> clazz) {
        return id(clazz.getSimpleName().toLowerCase());
    }

    public static String isClientOrServer(boolean isClient) {
        return isClient ? "Client-Side" : "Server-Side";
    }
}
