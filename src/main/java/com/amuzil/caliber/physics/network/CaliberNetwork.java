package com.amuzil.caliber.physics.network;

import com.amuzil.caliber.CaliberPhysics;
import com.amuzil.caliber.physics.network.impl.SendRigidBodyMovementPacket;
import com.amuzil.caliber.physics.network.impl.SendRigidBodyPropertiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class CaliberNetwork {
    private static final String VERSION = "1.0.0";

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(CaliberPhysics.MOD_ID).versioned(VERSION);
        registrar.playToClient(
                SendRigidBodyMovementPacket.TYPE,
                SendRigidBodyMovementPacket.STREAM_CODEC,
                PacketUtil::receiveClientMessage
        );

        registrar.playToClient(
                SendRigidBodyPropertiesPacket.TYPE,
                SendRigidBodyPropertiesPacket.STREAM_CODEC,
                PacketUtil::receiveClientMessage
        );
    }

    public static void sendToPlayersTrackingEntity(Entity entity, CaliberPacket payload) {
        PacketDistributor.sendToPlayersTrackingEntity(entity, payload);
    }

    public static void sendToClient(CaliberPacket payload, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendToServer(CaliberPacket payload) {
        PacketDistributor.sendToServer(payload);
    }
}
