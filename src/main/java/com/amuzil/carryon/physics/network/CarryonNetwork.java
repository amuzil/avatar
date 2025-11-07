package com.amuzil.carryon.physics.network;

import com.amuzil.carryon.CarryOn;
import com.amuzil.carryon.physics.network.impl.SendRigidBodyMovementPacket;
import com.amuzil.carryon.physics.network.impl.SendRigidBodyPropertiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = CarryOn.MOD_ID)
public class CarryonNetwork {
    private static final String PROTOCOL_VERSION = "1.0.0";

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(CarryOn.MOD_ID).versioned(PROTOCOL_VERSION);
        PacketUtil.registerToClient(MAIN, SendRigidBodyMovementPacket.class);
        PacketUtil.registerToClient(MAIN, SendRigidBodyPropertiesPacket.class);
    }

    public static void sendToClient(CarryonPacket payload, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendToServer(CarryonPacket payload) {
        PacketDistributor.sendToServer(payload);
    }
}
