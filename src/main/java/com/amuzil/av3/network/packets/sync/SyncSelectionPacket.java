package com.amuzil.av3.network.packets.sync;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.BendingSelection;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.network.packets.api.AvatarPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;
import java.util.UUID;

import static com.amuzil.av3.data.capability.AvatarCapabilities.getBender;

public class SyncSelectionPacket implements AvatarPacket {
    public static final Type<SyncSelectionPacket> TYPE = new Type<>(Avatar.id(SyncSelectionPacket.class));
    public static final StreamCodec<FriendlyByteBuf, SyncSelectionPacket> CODEC =
            StreamCodec.ofMember(SyncSelectionPacket::toBytes, SyncSelectionPacket::new);

    private final CompoundTag tag; // The NBT data to sync
    private final UUID playerUUID; // The UUID of the player

    public SyncSelectionPacket(CompoundTag tag, UUID playerUUID) {
        this.tag = tag;
        this.playerUUID = playerUUID;
    }

    public SyncSelectionPacket(FriendlyByteBuf buf) {
        this.tag = buf.readNbt();
        this.playerUUID = buf.readUUID();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
        buf.writeUUID(playerUUID);
    }

    public static void handle(SyncSelectionPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.flow().getReceptionSide().isServer()) {
                // Update Bender's BendingSelection on server
                ServerPlayer player = Objects.requireNonNull(ctx.player().getServer()).getPlayerList().getPlayer(msg.playerUUID);
                assert player != null;
                Bender bender = getBender(player);
                if (bender != null) {
                    BendingSelection newBendingSelection = new BendingSelection(msg.tag);
                    newBendingSelection.setOriginalBlocksMap(bender.getSelection().originalBlocksMap());
                    bender.setSelection(newBendingSelection);
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
