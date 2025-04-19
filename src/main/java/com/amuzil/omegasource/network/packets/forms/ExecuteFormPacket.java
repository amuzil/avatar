package com.amuzil.omegasource.network.packets.forms;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.bending.BendingForm;
import com.amuzil.omegasource.capability.AvatarCapabilities;
import com.amuzil.omegasource.entity.ElementProjectile;
import com.amuzil.omegasource.events.FormActivatedEvent;
import com.amuzil.omegasource.network.AvatarNetwork;
import com.amuzil.omegasource.network.packets.api.AvatarPacket;
import com.amuzil.omegasource.network.packets.client.FormActivatedPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

import static com.amuzil.omegasource.bending.BendingForms.*;


public class ExecuteFormPacket implements AvatarPacket {
    public BendingForm form;

    public ExecuteFormPacket(BendingForm form) {
        this.form = form;
    }

    public static void handle(ExecuteFormPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Work that needs to be thread-safe (most work)
            ServerPlayer player = ctx.get().getSender(); // the client that sent this packet
            assert player != null;
            ServerLevel level = player.serverLevel();
            Avatar.LOGGER.debug("Form Executed: {}", msg.form.name());

            MinecraftForge.EVENT_BUS.post(new FormActivatedEvent(msg.form, player, false));

            // Extra case for step
            if (msg.form.equals(STEP))
                AvatarNetwork.sendToServer(new ReleaseFormPacket(STEP));
//            ElementProjectile entity;
//            entity = ElementProjectile.createElementEntity(msg.form, Elements.FIRE, player, level);
//            int entityId = 0;
//            assert entity != null;
//            if (msg.form.equals(ARC) || msg.form.equals(NULL)) {
//                entity.control(1.5f, msg.form);
//            } else if (msg.form.equals(STRIKE) || msg.form.equals(BLOCK)) {
//                entity.shoot(player.getViewVector(1).x, player.getViewVector(1).y, player.getViewVector(1).z, 1, 1);
//            } else {
//                if (msg.form.equals(STEP))
//                    AvatarNetwork.sendToServer(new ReleaseFormPacket(STEP)); // Guarantee safe release to clean Magi's FormPath state
//                entity.discard();
//                return; // Unhandled Form - Discard and print no effects
//            }
//            level.addFreshEntity(entity);
//            FormActivatedPacket packet = new FormActivatedPacket(msg.form, Elements.FIRE, entity.getId());
//
//            /* Distribute packet to clients within 500 blocks | CLIENT */
//            AvatarNetwork.CHANNEL.send(PacketDistributor.NEAR.with(
//                    () -> new PacketDistributor.TargetPoint(player.getX(), player.getY(), player.getZ(),
//                            500, level.dimension())), packet);
        });

        ctx.get().setPacketHandled(true);
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUtf(form.name());
    }

    public static ExecuteFormPacket fromBytes(FriendlyByteBuf buffer) {
        String formName = buffer.readUtf();
        return new ExecuteFormPacket(new BendingForm(formName));
    }
}
