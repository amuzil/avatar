package com.amuzil.omegasource.network.packets.forms;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.Form;
import com.amuzil.omegasource.events.FormActivatedEvent;
import com.amuzil.omegasource.network.packets.api.AvatarPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class ReleaseFormPacket implements AvatarPacket {
    public Form form;

    public ReleaseFormPacket(Form form) {
        this.form = form;
    }

    public static void handle(ReleaseFormPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Work that needs to be thread-safe (most work)
            ServerPlayer sender = ctx.get().getSender(); // the client that sent this packet
            Avatar.LOGGER.debug("Form releasing: {}", msg.form.name());
            // Do stuff
            MinecraftForge.EVENT_BUS.post(new FormActivatedEvent(msg.form, sender, true));
        });
        ctx.get().setPacketHandled(true);
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUtf(form.name());
    }

    public static ReleaseFormPacket fromBytes(FriendlyByteBuf buffer) {
        String formName = buffer.readUtf();
        return new ReleaseFormPacket(new Form(formName));
    }
}
