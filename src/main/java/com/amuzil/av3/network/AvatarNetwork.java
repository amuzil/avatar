package com.amuzil.av3.network;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.network.packets.api.AvatarPacket;
import com.amuzil.av3.network.packets.bending.ChooseElementPacket;
import com.amuzil.av3.network.packets.form.ExecuteFormPacket;
import com.amuzil.av3.network.packets.form.ReleaseFormPacket;
import com.amuzil.av3.network.packets.sync.SyncMovementPacket;
import com.amuzil.av3.network.packets.sync.SyncSelectionPacket;
import com.amuzil.caliber.physics.network.impl.ForceCloudCollisionPacket;
import com.amuzil.caliber.physics.network.impl.ForceCloudSpawnPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class AvatarNetwork {
    private static final String VERSION = "1.0.0";

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Avatar.MOD_ID).versioned(VERSION);


        registrar.playToServer(
                SyncSelectionPacket.TYPE,
                SyncSelectionPacket.CODEC,
                SyncSelectionPacket::handle
        );

        /** Server **/
        registrar.playToServer(
                SyncMovementPacket.TYPE,
                SyncMovementPacket.CODEC,
                SyncMovementPacket::handle
        );
        registrar.playToServer(
                ExecuteFormPacket.TYPE,
                ExecuteFormPacket.CODEC,
                ExecuteFormPacket::handle
        );

        registrar.playToServer(
                ReleaseFormPacket.TYPE,
                ReleaseFormPacket.CODEC,
                ReleaseFormPacket::handle
        );

        /** Client **/
        registrar.playToClient(
                ForceCloudCollisionPacket.TYPE,
                ForceCloudCollisionPacket.CODEC,
                ForceCloudCollisionPacket::handle
        );

        registrar.playToClient(
                ForceCloudSpawnPacket.TYPE,
                ForceCloudSpawnPacket.CODEC,
                ForceCloudSpawnPacket::handle
        );

        registrar.playToServer(
                ChooseElementPacket.TYPE,
                ChooseElementPacket.CODEC,
                ChooseElementPacket::handle

        );
    }

    public static void sendToClient(AvatarPacket payload, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendToServer(AvatarPacket payload) {
        PacketDistributor.sendToServer(payload);
    }
}
