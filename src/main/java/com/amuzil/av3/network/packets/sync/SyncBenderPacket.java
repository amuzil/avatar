package com.amuzil.av3.network.packets.sync;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.network.packets.api.AvatarPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;
import java.util.UUID;

import static com.amuzil.av3.data.capability.AvatarCapabilities.getOrCreateBender;

@Deprecated
public class SyncBenderPacket implements AvatarPacket {
    public static final Type<SyncBenderPacket> TYPE = new Type<>(Avatar.id(SyncBenderPacket.class));
    public static final StreamCodec<FriendlyByteBuf, SyncBenderPacket> CODEC =
            StreamCodec.ofMember(SyncBenderPacket::toBytes, SyncBenderPacket::new);

    private final CompoundTag tag; // The NBT data to sync
    private final UUID playerUUID; // Entity ID to send back to client

    public SyncBenderPacket(CompoundTag tag, UUID playerUUID) {
        this.tag = tag;
        this.playerUUID = playerUUID;
    }

    public SyncBenderPacket(FriendlyByteBuf buf) {
        this.tag = buf.readNbt();
        this.playerUUID = buf.readUUID();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
        buf.writeUUID(playerUUID);
    }

    public static void handle(SyncBenderPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.flow().getReceptionSide().isClient()) {
                // Update Bender's data on their client
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null) {
                    Bender bender = getOrCreateBender(player);
                    if (bender != null) {
                        bender.deserializeNBT(player.level().registryAccess(), msg.tag);
                        bender.markClean();
                    }
                }
            } else {
                // Update Bender's data on server
                ServerPlayer player = Objects.requireNonNull(ctx.player().getServer()).getPlayerList().getPlayer(msg.playerUUID);
                assert player != null;
                Bender bender = getOrCreateBender(player);
                if (bender != null) {
                    bender.deserializeNBT(player.level().registryAccess(), msg.tag);
                    bender.markClean();
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}