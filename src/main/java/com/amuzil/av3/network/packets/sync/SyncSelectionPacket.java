package com.amuzil.av3.network.packets.sync;

import com.amuzil.av3.bending.BendingSelection;
import com.amuzil.av3.capability.AvatarCapabilities;
import com.amuzil.av3.network.packets.api.AvatarPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;


public class SyncSelectionPacket implements AvatarPacket {
    private final CompoundTag tag; // The NBT data to sync
    private final UUID playerUUID; // The UUID of the player

    public SyncSelectionPacket(CompoundTag tag, UUID playerUUID) {
        this.tag = tag;
        this.playerUUID = playerUUID;
    }

    public static SyncSelectionPacket fromBytes(FriendlyByteBuf buf) {
        return new SyncSelectionPacket(buf.readNbt(), buf.readUUID());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
        buf.writeUUID(playerUUID);
    }

    public static boolean handle(SyncSelectionPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            if (ctx.getDirection().getReceptionSide().isServer()) {
                // Update Bender's BendingSelection on the server
                ServerPlayer player = Objects.requireNonNull(ctx.getSender()).server.getPlayerList().getPlayer(msg.playerUUID);
                assert player != null;
                player.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
                    BendingSelection newBendingSelection = new BendingSelection(msg.tag);
                    newBendingSelection.setOriginalBlocksMap(bender.getSelection().originalBlocksMap());
                    bender.setSelection(newBendingSelection);
                    bender.markClean();
                });
            }
        });
        ctx.setPacketHandled(true);
        return true;
    }
}