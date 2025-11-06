package com.amuzil.av3.network.packets.form;

import com.amuzil.magus.form.ActiveForm;
import com.amuzil.av3.events.FormActivatedEvent;
import com.amuzil.av3.network.packets.api.AvatarPacket;
import com.amuzil.av3.Avatar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;

public class ReleaseFormPacket implements AvatarPacket {
    public static final Type<ReleaseFormPacket> TYPE = new Type<>(Avatar.id(ReleaseFormPacket.class));
    public static final StreamCodec<FriendlyByteBuf, ReleaseFormPacket> CODEC =
            StreamCodec.ofMember(ReleaseFormPacket::toBytes, ReleaseFormPacket::new);

    private final CompoundTag tag;

    public ReleaseFormPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public ReleaseFormPacket(FriendlyByteBuf buf) {
        this.tag = new ActiveForm(buf.readNbt()).serializeNBT();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeNbt(tag);
    }

    public static void handle(ReleaseFormPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.flow().getReceptionSide().isServer()) {
                // Server-side handling (packet was sent from client to server)
                handleServerSide(msg.tag, Objects.requireNonNull((ServerPlayer) ctx.player()));
            }
        });
    }

    public static void handleServerSide(CompoundTag tag, ServerPlayer player) {
        ActiveForm activeForm = new ActiveForm(tag);
//        Avatar.LOGGER.info("Form Released: {}", activeForm.form().name());
        NeoForge.EVENT_BUS.post(new FormActivatedEvent(activeForm, player, true));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
