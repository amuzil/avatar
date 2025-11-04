package com.amuzil.av3.network;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.network.packets.api.AvatarPacket;
import com.amuzil.av3.network.packets.form.ExecuteFormPacket;
import com.amuzil.av3.network.packets.form.ReleaseFormPacket;
import com.amuzil.av3.network.packets.skill.ActivatedSkillPacket;
import com.amuzil.av3.network.packets.skill.SkillDataPacket;
import com.amuzil.av3.network.packets.sync.SyncBenderPacket;
import com.amuzil.av3.network.packets.sync.SyncMovementPacket;
import com.amuzil.av3.network.packets.sync.SyncSelectionPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class AvatarNetwork {
    private static final String PROTOCOL_VERSION = "1.0.0";

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Avatar.MOD_ID).versioned(PROTOCOL_VERSION);

        registrar.playBidirectional(
                SyncBenderPacket.TYPE,
                SyncBenderPacket.CODEC,
                SyncBenderPacket::handle
        );

        registrar.playBidirectional(
                SyncSelectionPacket.TYPE,
                SyncSelectionPacket.CODEC,
                SyncSelectionPacket::handle
        );

        registrar.playToClient(
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
    }

    public static void sendToClient(AvatarPacket payload, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendToServer(AvatarPacket payload) {
        PacketDistributor.sendToServer(payload);
    }
}
