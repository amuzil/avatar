package com.amuzil.omegasource.network;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.network.packets.api.AvatarPacket;
import com.amuzil.omegasource.network.packets.client.FormActivatedPacket;
import com.amuzil.omegasource.network.packets.client.SyncBenderPacket;
import com.amuzil.omegasource.network.packets.client.SyncFormPathPacket;
import com.amuzil.omegasource.network.packets.forms.ExecuteFormPacket;
import com.amuzil.omegasource.network.packets.forms.ReleaseFormPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;


public class AvatarNetwork {
    private static final String PROTOCOL_VERSION = "1.0.0";
    private static int packetId = 0;
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "main"),
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

        CHANNEL.messageBuilder(SyncBenderPacket.class, nextID())
                .encoder(SyncBenderPacket::toBytes)
                .decoder(SyncBenderPacket::fromBytes)
                .consumerMainThread(SyncBenderPacket::handle)
                .add();

        CHANNEL.messageBuilder(SyncFormPathPacket.class, nextID())
                .encoder(SyncFormPathPacket::toBytes)
                .decoder(SyncFormPathPacket::fromBytes)
                .consumerMainThread(SyncFormPathPacket::handle)
                .add();
    }

    public static void sendToClient(AvatarPacket packet, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendToServer(AvatarPacket packet) {
        CHANNEL.sendToServer(packet);
    }
}
