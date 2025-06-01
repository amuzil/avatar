package com.amuzil.omegasource.network.packets.sync;

import com.amuzil.omegasource.capability.AvatarCapabilities;
import com.amuzil.omegasource.network.packets.api.AvatarPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;


public class SyncMovementPacket implements AvatarPacket {
    private final Vec3 movement;
    private final UUID playerUUID;

    public SyncMovementPacket(Vec3 movement, UUID playerUUID) {
        this.movement = movement;
        this.playerUUID = playerUUID;
    }

    public static SyncMovementPacket fromBytes(FriendlyByteBuf buf) {
        return new SyncMovementPacket(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()), buf.readUUID());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDouble(movement.x());
        buf.writeDouble(movement.y());
        buf.writeDouble(movement.z());
        buf.writeUUID(playerUUID);
    }

    public static boolean handle(SyncMovementPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            if (ctx.getDirection().getReceptionSide().isServer()) {
                // Update Bender's BendingSelection on the server
                ServerPlayer player = Objects.requireNonNull(ctx.getSender()).server.getPlayerList().getPlayer(msg.playerUUID);
                assert player != null;
                player.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
                    bender.setDeltaMovement(msg.movement);
                    bender.markClean();
                });
            }
        });
        ctx.setPacketHandled(true);
        return true;
    }
}