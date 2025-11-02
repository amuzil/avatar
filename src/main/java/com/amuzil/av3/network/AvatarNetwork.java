package com.amuzil.av3.network;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.network.packets.api.AvatarPacket;
import com.amuzil.av3.network.packets.form.ActivatedFormPacket;
import com.amuzil.av3.network.packets.form.ExecuteFormPacket;
import com.amuzil.av3.network.packets.form.ReleaseFormPacket;
import com.amuzil.av3.network.packets.skill.ActivatedSkillPacket;
import com.amuzil.av3.network.packets.skill.SkillDataPacket;
import com.amuzil.av3.network.packets.sync.SyncBenderPacket;
import com.amuzil.av3.network.packets.sync.SyncMovementPacket;
import com.amuzil.av3.network.packets.sync.SyncSelectionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.NetworkRegistry;


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
        CHANNEL.messageBuilder(ActivatedSkillPacket.class, nextID())
                .encoder(ActivatedSkillPacket::toBytes)
                .decoder(ActivatedSkillPacket::fromBytes)
                .consumerMainThread(ActivatedSkillPacket::handle)
                .add();

        CHANNEL.messageBuilder(ActivatedFormPacket.class, nextID())
                .encoder(ActivatedFormPacket::toBytes)
                .decoder(ActivatedFormPacket::fromBytes)
                .consumerMainThread(ActivatedFormPacket::handle)
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

        CHANNEL.messageBuilder(SyncSelectionPacket.class, nextID())
                .encoder(SyncSelectionPacket::toBytes)
                .decoder(SyncSelectionPacket::fromBytes)
                .consumerMainThread(SyncSelectionPacket::handle)
                .add();

        CHANNEL.messageBuilder(SyncMovementPacket.class, nextID())
                .encoder(SyncMovementPacket::toBytes)
                .decoder(SyncMovementPacket::fromBytes)
                .consumerMainThread(SyncMovementPacket::handle)
                .add();

        CHANNEL.messageBuilder(SkillDataPacket.class, nextID())
                .encoder(SkillDataPacket::toBytes)
                .decoder(SkillDataPacket::fromBytes)
                .consumerMainThread(SkillDataPacket::handle)
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
