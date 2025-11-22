package com.amuzil.av3.network.packets.bender;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.element.Elements;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.network.packets.api.AvatarPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;
import java.util.UUID;

import static com.amuzil.av3.data.capability.AvatarCapabilities.getOrCreateBender;

public class ToggleBendingPacket implements AvatarPacket {
    public static final Type<ToggleBendingPacket> TYPE = new Type<>(Avatar.id(ToggleBendingPacket.class));
    public static final StreamCodec<FriendlyByteBuf, ToggleBendingPacket> CODEC =
            StreamCodec.ofMember(ToggleBendingPacket::toBytes, ToggleBendingPacket::new);

    private final UUID playerUUID;
    private final boolean active;

    public ToggleBendingPacket(FriendlyByteBuf buf) {
        this.playerUUID = buf.readUUID();
        this.active = buf.readBoolean();
    }

    public ToggleBendingPacket(UUID playerUUID, boolean active) {
        this.playerUUID = playerUUID;
        this.active = active;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(playerUUID);
        buf.writeBoolean(active);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ToggleBendingPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!ctx.flow().getReceptionSide().isClient()) {
                ServerPlayer player = Objects.requireNonNull(ctx.player().getServer()).getPlayerList().getPlayer(msg.playerUUID);
                assert player != null;
                Bender bender = getOrCreateBender(player);
                if (bender != null) {
                    bender.setBending(msg.active);
                }
            }
        });
    }
}
