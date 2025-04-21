package com.amuzil.omegasource.network.packets.client;

import com.amuzil.omegasource.capability.AvatarCapabilities;
import com.amuzil.omegasource.network.packets.api.AvatarPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncBenderPacket implements AvatarPacket {
    private final CompoundTag tag; // The NBT data to sync
    private final UUID playerUUID; // Entity ID to send back to client

    public SyncBenderPacket(CompoundTag tag, UUID playerUUID) {
        this.tag = tag;
        this.playerUUID = playerUUID;
    }

    public static SyncBenderPacket fromBytes(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
        UUID playerUUID = buf.readUUID();
        return new SyncBenderPacket(tag, playerUUID);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
        buf.writeUUID(playerUUID);
    }

    public static boolean handle(SyncBenderPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            if (ctx.getDirection().getReceptionSide().isClient()) {
                // Update Bender's data on their client
                LocalPlayer player = Minecraft.getInstance().player;
                assert player != null;
                player.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
                            System.out.printf("Changed element from %s to %s\n", bender.getElement().name(), msg.tag.getString("Active Element"));
                            bender.deserializeNBT(msg.tag);
                            bender.markClean();
                        });
            } else {
                // Update Bender's data on server
                ServerPlayer player = Objects.requireNonNull(ctx.getSender()).server.getPlayerList().getPlayer(msg.playerUUID);
                assert player != null;
                player.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
                    bender.deserializeNBT(msg.tag);
                    bender.markClean();
                });
            }
        });
        ctx.setPacketHandled(true);
        return true;
    }
}