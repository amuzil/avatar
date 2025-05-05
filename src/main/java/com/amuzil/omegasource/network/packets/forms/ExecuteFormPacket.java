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

import static com.amuzil.omegasource.bending.BendingForms.*;


public class ExecuteFormPacket implements AvatarPacket {
    public BendingForm form;

    public ExecuteFormPacket(BendingForm form) {
        this.form = form;
    }

    public static void handleServerSide(BendingForm form, ServerPlayer player) {
        // Work that needs to be thread-safe (most work)
        assert player != null;
        ServerLevel level = player.serverLevel();
        Avatar.LOGGER.debug("Form Executed: {}", form.name());

        MinecraftForge.EVENT_BUS.post(new FormActivatedEvent(form, player, false));

        // Extra case for step
        if (form.equals(STEP))
            ReleaseFormPacket.handleServerSide(STEP, player);
    }

    public static void handle(ExecuteFormPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isServer())
                handleServerSide(msg.form, Objects.requireNonNull(ctx.get().getSender()));
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
