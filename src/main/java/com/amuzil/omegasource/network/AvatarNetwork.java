package com.amuzil.omegasource.network;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.network.packets.api.AvatarPacket;
import com.amuzil.omegasource.network.packets.client.FormActivatedPacket;
import com.amuzil.omegasource.network.packets.forms.ExecuteFormPacket;
import com.amuzil.omegasource.network.packets.forms.ReleaseFormPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;


public class AvatarNetwork {
    private static final String PROTOCOL_VERSION = "1.0.0";
    private static int packetId = 0;
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Avatar.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int nextID() {
        return packetId++;
    }

    public static void register() {
        CHANNEL.messageBuilder(FormActivatedPacket.class, nextID())
                .encoder(FormActivatedPacket::toBytes)
                .decoder(FormActivatedPacket::fromBytes)
                .consumerMainThread(FormActivatedPacket::handle)
                .add();

        CHANNEL.messageBuilder(ExecuteFormPacket.class, nextID())
                .encoder(ExecuteFormPacket::toBytes)
                .decoder(ExecuteFormPacket::fromBytes)
                .consumerMainThread(ExecuteFormPacket::handle)
                .add();

        CHANNEL.messageBuilder(ReleaseFormPacket.class, nextID())
                .encoder(ReleaseFormPacket::toBytes)
                .decoder(ReleaseFormPacket::fromBytes)
                .consumerMainThread(ReleaseFormPacket::handle)
                .add();
    }

    public static void sendToClient(AvatarPacket packet, ServerPlayer player) {
        CHANNEL.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(AvatarPacket packet) {
        CHANNEL.sendToServer(packet);
    }
}
