package com.amuzil.omegasource.network.packets.sync;

import com.amuzil.omegasource.capability.AvatarCapabilities;
import com.amuzil.omegasource.network.packets.api.AvatarPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class SyncFormPathPacket implements AvatarPacket {
    private final CompoundTag tag; // The NBT data to sync

    public SyncFormPathPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public static SyncFormPathPacket fromBytes(FriendlyByteBuf buf) {
        return new SyncFormPathPacket(buf.readNbt());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }

    public static boolean handle(SyncFormPathPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            if (ctx.getDirection().getReceptionSide().isClient()) {
                // Update Bender's FormPath on their client
                LocalPlayer player = Minecraft.getInstance().player;
                assert player != null;
                player.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
//                    bender.getFormPath().deserializeNBT(msg.tag);
//                    why do we need to sync this? we're sending these forms from client to server,
//                    sending it back again is a bit roundabout?
                    bender.markClean();
                });
            }
        });
        ctx.setPacketHandled(true);
        return true;
    }
}