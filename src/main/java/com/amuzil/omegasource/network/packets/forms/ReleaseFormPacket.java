package com.amuzil.omegasource.network.packets.forms;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.events.FormActivatedEvent;
import com.amuzil.omegasource.network.packets.api.AvatarPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;


public class ReleaseFormPacket implements AvatarPacket {
    private final CompoundTag tag;

    public ReleaseFormPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public static void handleServerSide(CompoundTag tag, ServerPlayer player) {
        // Work that needs to be thread-safe (most work)
        assert player != null;
        ServerLevel level = player.serverLevel();
        ActiveForm activeForm = new ActiveForm(tag);
//        Avatar.LOGGER.info("Form Released: {}", activeForm.form().name());

        MinecraftForge.EVENT_BUS.post(new FormActivatedEvent(activeForm, player, true));
    }

    public static void handle(ReleaseFormPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isServer())
                handleServerSide(msg.tag, Objects.requireNonNull(ctx.get().getSender()));
        });
        ctx.get().setPacketHandled(true);
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeNbt(tag);
    }

    public static ReleaseFormPacket fromBytes(FriendlyByteBuf buffer) {
        return new ReleaseFormPacket(new ActiveForm(buffer.readNbt()).serializeNBT());
    }
}
