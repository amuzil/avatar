package com.amuzil.omegasource.network.packets.form;

import com.amuzil.omegasource.entity.projectile.AvatarProjectile;
import com.amuzil.omegasource.network.AvatarNetwork;
import com.amuzil.omegasource.network.packets.api.AvatarPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Predicate;
import java.util.function.Supplier;

@Deprecated
public class ActivatedFormPacket implements AvatarPacket {

    private final int entityId; // Entity ID to send back to client for FX

    public ActivatedFormPacket(int entityId) {
        this.entityId = entityId;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
    }

    public static ActivatedFormPacket fromBytes(FriendlyByteBuf buf) {
        int entityId = buf.readInt();
        return new ActivatedFormPacket(entityId);
    }

    // Server-side handler
    public static void handleServerSide(int entityId, ServerPlayer player) {
        // Perform server-side entity spawning and updating logic and fire Form Event here
        ServerLevel level = player.serverLevel();
        ActivatedFormPacket packet = new ActivatedFormPacket(entityId);
        Predicate<ServerPlayer> predicate = (serverPlayer) -> player.distanceToSqr(serverPlayer) < 2500;
        for (ServerPlayer nearbyPlayer: level.getPlayers(predicate.and(LivingEntity::isAlive))) {
            AvatarNetwork.sendToClient(packet, nearbyPlayer);
        }

        // Keep this in case we want a more specific client packet distribution filter
        /** Distribute packet to clients within 500 blocks **/
//        AvatarNetwork.CHANNEL.send(PacketDistributor.NEAR.with(
//                () -> new PacketDistributor.TargetPoint(player.getX(), player.getY(), player.getZ(),
//                        500, level.dimension())), packet);
    }

    // Client-side handler
    @OnlyIn(Dist.CLIENT)
    private static void handleClientSide(int entityId) {
        // Perform client-side particle effect or other rendering logic here
        Player player = Minecraft.getInstance().player;
        assert player != null;
        AvatarProjectile projectile = (AvatarProjectile) player.level().getEntity(entityId);
        /**
         NOTE: Need to ensure ElementProjectile's extra constructor args are set client-side.
         @see AvatarProjectile#AvatarProjectile(EntityType, Level) This gets called first and server-side only.
         Can't change this default constructor because it's needed to register entities. Add/use any extra args to Packet.
         */
//        assert projectile != null;
//        projectile.startEffect(STRIKE);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClientSide(entityId));
            } else {
                ServerPlayer player = ctx.get().getSender();
                assert player != null;
                handleServerSide(entityId, player);
            }
        });
        return true;
    }
}
