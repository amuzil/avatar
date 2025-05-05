package com.amuzil.omegasource.network.packets.forms;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.bending.BendingForm;
import com.amuzil.omegasource.events.FormActivatedEvent;
import com.amuzil.omegasource.network.packets.api.AvatarPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;


public class ReleaseFormPacket implements AvatarPacket {
    public BendingForm form;

    public ReleaseFormPacket(BendingForm form) {
        this.form = form;
    }

    public static void handleServerSide(BendingForm form, ServerPlayer player) {
        // Work that needs to be thread-safe (most work)
        assert player != null;
        ServerLevel level = player.serverLevel();
        Avatar.LOGGER.debug("Form Released: {}", form.name());

        MinecraftForge.EVENT_BUS.post(new FormActivatedEvent(form, player, true));
    }

    public static void handle(ReleaseFormPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isServer())
                handleServerSide(msg.form, Objects.requireNonNull(ctx.get().getSender()));
        });
        ctx.get().setPacketHandled(true);
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUtf(form.name());
    }

    public static ReleaseFormPacket fromBytes(FriendlyByteBuf buffer) {
        String formName = buffer.readUtf();
        return new ReleaseFormPacket(new BendingForm(formName));
    }
}
