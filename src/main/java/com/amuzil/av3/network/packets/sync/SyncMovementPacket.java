package com.amuzil.av3.network.packets.sync;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.capability.AvatarCapabilities;
import com.amuzil.av3.capability.IBender;
import com.amuzil.av3.network.packets.api.AvatarPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;
import java.util.UUID;

public class SyncMovementPacket implements AvatarPacket {
    public static final Type<SyncMovementPacket> TYPE = new Type<>(Avatar.id(SyncMovementPacket.class));
    public static final StreamCodec<FriendlyByteBuf, SyncMovementPacket> CODEC =
            StreamCodec.ofMember(SyncMovementPacket::toBytes, SyncMovementPacket::new);

    private final Vec3 movement;
    private final UUID playerUUID;

    public SyncMovementPacket(Vec3 movement, UUID playerUUID) {
        this.movement = movement;
        this.playerUUID = playerUUID;
    }

    public SyncMovementPacket(FriendlyByteBuf buf) {
        this.movement = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.playerUUID = buf.readUUID();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDouble(movement.x());
        buf.writeDouble(movement.y());
        buf.writeDouble(movement.z());
        buf.writeUUID(playerUUID);
    }

    public static void handle(SyncMovementPacket msg, IPayloadContext ctx) {
        System.out.println("MADE IT?!");
        ctx.enqueueWork(() -> {
            if (ctx.flow().getReceptionSide().isServer()) {
                // Update Bender's movement on server
                ServerPlayer player = Objects.requireNonNull(ctx.player().getServer()).getPlayerList().getPlayer(msg.playerUUID);
                assert player != null;
                IBender bender = player.getCapability(AvatarCapabilities.BENDER);
                if (bender != null) {
                    bender.setDeltaMovement(msg.movement);
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
