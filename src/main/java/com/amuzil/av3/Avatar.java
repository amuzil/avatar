package com.amuzil.av3;

import com.amuzil.av3.bending.BendingSkill;
import com.amuzil.av3.data.attachment.AvatarAttachments;
import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.collision.*;
import com.amuzil.av3.entity.api.modules.force.*;
import com.amuzil.av3.entity.api.modules.entity.GrowModule;
import com.amuzil.av3.entity.api.modules.entity.TimeoutModule;
import com.amuzil.av3.entity.api.modules.render.PhotonModule;
import com.amuzil.av3.entity.api.modules.render.SoundModule;
import com.amuzil.av3.input.InputModule;
import com.amuzil.av3.network.AvatarNetwork;
import com.amuzil.av3.utils.commands.AvatarCommands;
import com.amuzil.av3.utils.sound.AvatarSounds;
import com.amuzil.caliber.CaliberPhysics;
import com.amuzil.magus.registry.Registries;
import com.amuzil.magus.tree.SkillTree;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(Avatar.MOD_ID)
public class Avatar {
    public static final String MOD_ID = "av3";
    public static final Logger LOGGER = LogManager.getLogger();
    public static InputModule inputModule;

    public Avatar(IEventBus modEventBus, ModContainer modContainer) {
        CaliberPhysics.init(modEventBus, modContainer);

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::setupClient);
        modEventBus.addListener(AvatarNetwork::register);

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);

        AvatarAttachments.register(modEventBus);
        AvatarEntities.register(modEventBus);
        AvatarSounds.register(modEventBus);

        Registries.registerForms();
        Registries.FORMS_REGISTER.register(modEventBus);
        Registries.SKILL_REGISTER.register(modEventBus);
        Registries.SKILL_CATEGORY_REGISTER.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        // modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void setup(final FMLCommonSetupEvent event) {

        ModuleRegistry.register(MoveModule::new);
        ModuleRegistry.register(CurveModule::new);
        ModuleRegistry.register(ControlModule::new);
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
    }

    private void setupClient(final FMLClientSetupEvent event) {
        // Initialize the input modules
        inputModule = new InputModule();
    }

//    private void enqueueIMC(final InterModEnqueueEvent event) {}

//    private void processIMC(final InterModProcessEvent event) {}

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Setting up Avatar Mod commands...");
        AvatarCommands.register(event.getServer().getCommands().getDispatcher());

        // Initialize Skill Tree
        SkillTree.clear();
        Registries.SKILLS.stream().forEach(skill -> {
            SkillTree.RegisterSkill(((BendingSkill) skill).element(), /* toRegister.targetType(), */
                    skill.startPaths(), skill);
        });
    }

    @EventBusSubscriber(modid = MOD_ID)
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

    public static ResourceLocation id(Class<?> clazz) {
        return id(clazz.getSimpleName().toLowerCase());
    }

    public static String isClientOrServer(boolean isClient) {
        return isClient ? "Client-Side" : "Server-Side";
    }

//    private static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
//        // This will be invoked for every new player
//        return new ModifierLayer<>();
//    }
}
