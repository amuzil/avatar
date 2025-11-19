package com.amuzil.av3.network.packets.bending;

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

import static com.amuzil.av3.data.capability.AvatarCapabilities.getBender;

public class ChooseElementPacket implements AvatarPacket {
    public static final Type<ChooseElementPacket> TYPE = new Type<>(Avatar.id(ChooseElementPacket.class));
    public static final StreamCodec<FriendlyByteBuf, ChooseElementPacket> CODEC =
            StreamCodec.ofMember(ChooseElementPacket::toBytes, ChooseElementPacket::new);

    private final UUID playerUUID;
    private final String element;

    public ChooseElementPacket(FriendlyByteBuf buf) {
        this.playerUUID = buf.readUUID();
        this.element = buf.readUtf();
    }

    public ChooseElementPacket(UUID playerUUID, String element) {
        this.playerUUID = playerUUID;
        this.element = element;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(playerUUID);
        buf.writeUtf(element);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ChooseElementPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!ctx.flow().getReceptionSide().isClient()) {
                ServerPlayer player = Objects.requireNonNull(ctx.player().getServer()).getPlayerList().getPlayer(msg.playerUUID);
                assert player != null;
                Bender bender = getBender(player);
                if (bender != null) {
                    bender.setElement(Elements.get(ResourceLocation.parse(msg.element)));
                    bender.markDirty();
                }
            }
        });
    }
}
