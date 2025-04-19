package com.amuzil.omegasource.network.packets.client;

import com.amuzil.omegasource.capability.AvatarCapabilities;
import com.amuzil.omegasource.network.packets.api.AvatarPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncBenderPacket implements AvatarPacket {
    private final CompoundTag tag; // The NBT data to sync
    private final int entityId; // Entity ID to send back to client

    public SyncBenderPacket(CompoundTag tag, int entityId) {
        this.tag = tag;
        this.entityId = entityId;
    }

    public static SyncBenderPacket fromBytes(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
        int entityId = buf.readInt();
        return new SyncBenderPacket(tag, entityId);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
        buf.writeInt(entityId);
    }

    public static boolean handle(SyncBenderPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            if (ctx.getDirection().getReceptionSide().isClient()) {
                System.out.println("CLIENT SIDE: SyncBenderPacket");
                // Update Bender's data on their client
                LocalPlayer player = Minecraft.getInstance().player;
                player.getCapability(AvatarCapabilities.BENDER).ifPresent(
                        bender -> bender.deserializeNBT(msg.tag));
            } else {
                System.out.println("SERVER SIDE: SyncBenderPacket");
                // Update Bender's data on server
                ServerPlayer player = ctx.getSender();
                assert player != null;
                player.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
                    System.out.printf("Changed element from %s to %s\n", bender.getElement(), msg.tag.getString("Element"));
                    bender.setElement(msg.tag.getString("Element"));
                });
            }
        });
        ctx.setPacketHandled(true);
        return true;
    }
}