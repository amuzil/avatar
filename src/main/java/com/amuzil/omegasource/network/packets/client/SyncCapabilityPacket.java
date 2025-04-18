package com.amuzil.omegasource.network.packets.client;

import com.amuzil.omegasource.api.magus.capability.CapabilityHandler;
import com.amuzil.omegasource.network.packets.api.AvatarPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncCapabilityPacket implements AvatarPacket {
    // The NBT data to sync
    private final CompoundTag data;

    /**
     * Server‑side constructor: build from existing data
     */
    public SyncCapabilityPacket(CompoundTag data) {
        this.data = data;
    }

    /**
     * Decoder: reconstruct from bytes
     */
    public static SyncCapabilityPacket fromBytes(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();     // readNbt / writeNbt order must match :contentReference[oaicite:3]{index=3}
        return new SyncCapabilityPacket(tag);
    }

    /**
     * Encoder: write into buffer
     */
    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(data);                  // FriendlyByteBuf helper for CompoundTag :contentReference[oaicite:4]{index=4}
    }

    /**
     * Handler: invoked on the receiving side
     */
    public static boolean handle(SyncCapabilityPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        // Always enqueue work to run on the main thread :contentReference[oaicite:5]{index=5}
        ctx.enqueueWork(() -> {
            // Only run client‑side code if this packet was sent to the client
            if (ctx.getDirection().getReceptionSide().isClient()) {
                LocalPlayer player = Minecraft.getInstance().player;
                // Apply the NBT to the capability :contentReference[oaicite:6]{index=6}
                player.getCapability(CapabilityHandler.LIVING_DATA)
                        .ifPresent(cap -> cap.deserializeNBT(msg.data));
            }
        });
        ctx.setPacketHandled(true);           // mark as handled :contentReference[oaicite:7]{index=7}
        return true;
    }
}
