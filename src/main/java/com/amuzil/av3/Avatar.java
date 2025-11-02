package com.amuzil.av3;

import com.amuzil.carryon.example.entity.RayonExampleEntities;
import com.amuzil.carryon.example.renderer.RayonExampleEntityRenderers;
import com.amuzil.carryon.physics.bullet.collision.space.generator.PressureGenerator;
import com.amuzil.carryon.physics.bullet.collision.space.generator.TerrainGenerator;
import com.amuzil.carryon.physics.bullet.natives.NativeLoader;
import com.amuzil.carryon.physics.event.ClientEventHandler;
import com.amuzil.carryon.physics.event.ServerEventHandler;
import com.amuzil.carryon.physics.packet.RayonPacketHandlers;
import com.amuzil.magus.registry.Registries;
import com.amuzil.magus.tree.SkillTree;
import com.amuzil.av3.bending.BendingSkill;
import com.amuzil.av3.bending.element.Elements;
import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.entity.modules.ModuleRegistry;
import com.amuzil.av3.entity.modules.collision.*;
import com.amuzil.av3.entity.modules.entity.GrowModule;
import com.amuzil.av3.entity.modules.entity.SoundModule;
import com.amuzil.av3.entity.modules.entity.TimeoutModule;
import com.amuzil.av3.entity.modules.force.*;
import com.amuzil.av3.entity.modules.render.PhotonModule;
import com.amuzil.av3.input.InputModule;
import com.amuzil.av3.network.AvatarNetwork;
import com.amuzil.av3.utils.commands.AvatarCommands;
import com.amuzil.av3.utils.sound.AvatarSounds;
import net.minecraft.client.Minecraft;
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
    public static final String MOD_ID = "av3";
    public static final Logger LOGGER = LogManager.getLogger();
    public static InputModule inputModule;

    public Avatar(FMLJavaModLoadingContext context) {
        NativeLoader.load();
        IEventBus modEventBus = context.getModEventBus();
        // Register the setup method for mod loading
        modEventBus.addListener(this::setup);
        // Register the enqueueIMC method for mod loading
        modEventBus.addListener(this::enqueueIMC);
        // Register the processIMC method for mod loading
        modEventBus.addListener(this::processIMC);
        // Register the setupClient method for mod loading
        modEventBus.addListener(this::setupClient);

        // Rayon Rigid Body Physics
        modEventBus.addListener(RayonExampleEntityRenderers::registerEntityRenderers);
        RayonExampleEntities.register(modEventBus);
        java.util.logging.LogManager.getLogManager().reset(); // prevent annoying libbulletjme spam

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        AvatarEntities.register(modEventBus);
        AvatarSounds.register(modEventBus);

//        Registries.SKILL_CATEGORY_REGISTER.register(modEventBus);
        Elements.SKILL_CATEGORY_REGISTER.register(modEventBus); // Why not register SKILL_CATEGORY_REGISTER in Registries?
        Registries.SKILL_REGISTER.register(modEventBus);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some pre init code
        AvatarNetwork.register();

        ModuleRegistry.register(MoveModule::new);
        ModuleRegistry.register(CurveModule::new);
        ModuleRegistry.register(TimeoutModule::new);
        ModuleRegistry.register(SoundModule::new);
        ModuleRegistry.register(GrowModule::new);
        ModuleRegistry.register(SimpleKnockbackModule::new);
        ModuleRegistry.register(FireModule::new);
        ModuleRegistry.register(SimpleDamageModule::new);
        ModuleRegistry.register(ChangeSpeedModule::new);
        ModuleRegistry.register(PhotonModule::new);
        ModuleRegistry.register(BindModule::new);
        ModuleRegistry.register(OrbitModule::new);
        ModuleRegistry.register(AirCollisionModule::new);
        ModuleRegistry.register(FireCollisionModule::new);
        ModuleRegistry.register(WaterCollisionModule::new);
        ModuleRegistry.register(FireEffectModule::new);

        // Rayon Rigid Body Physics
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.register(ServerEventHandler.class);
        forgeBus.register(PressureGenerator.class);
        forgeBus.register(TerrainGenerator.class);
    }

    private void setupClient(final FMLClientSetupEvent event) {
        // Initialize the input modules
        inputModule = new InputModule();

        // Rayon Rigid Body Physics
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.register(ClientEventHandler.class);
        RayonPacketHandlers.registerPackets();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {}

    private void processIMC(final InterModProcessEvent event) {}

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Setting up Avatar Mod commands...");
        AvatarCommands.register(event.getServer().getCommands().getDispatcher());

        // Initialize Skill Tree
        SkillTree.clear();
        Registries.SKILLS.get().getValues().forEach(c -> SkillTree.RegisterSkill(((BendingSkill)c).element(), /* toRegister.targetType(), */c.startPaths(), c));
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Setting up Avatar Mod client...");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

//            PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
//                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "animation"),
//                    42, Avatar::registerPlayerAnimation);
        }
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static String isClientOrServer(boolean isClient) {
        return isClient ? "Client-Side" : "Server-Side";
    }

//    private static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
//        // This will be invoked for every new player
//        return new ModifierLayer<>();
//    }
}
