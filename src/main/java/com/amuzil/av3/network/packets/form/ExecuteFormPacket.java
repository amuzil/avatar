package com.amuzil.av3.network.packets.form;

import com.amuzil.magus.form.ActiveForm;
import com.amuzil.av3.events.FormActivatedEvent;
import com.amuzil.av3.network.packets.api.AvatarPacket;
import com.amuzil.av3.Avatar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;

import static com.amuzil.av3.bending.form.BendingForms.STEP;

public class ExecuteFormPacket implements AvatarPacket {
    public static final Type<ExecuteFormPacket> TYPE = new Type<>(Avatar.id(ExecuteFormPacket.class));
    public static final StreamCodec<FriendlyByteBuf, ExecuteFormPacket> CODEC =
            StreamCodec.ofMember(ExecuteFormPacket::toBytes, ExecuteFormPacket::new);

    private final CompoundTag tag;

    public ExecuteFormPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public ExecuteFormPacket(FriendlyByteBuf buf) {
        this.tag = new ActiveForm(buf.readNbt()).serializeNBT();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeNbt(tag);
    }

    public static void handle(ExecuteFormPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.flow().getReceptionSide().isServer()) {
                // Server-side handling (packet was sent from client to server)
                handleServerSide(msg.tag, Objects.requireNonNull((ServerPlayer) ctx.player()));
            }
        });
    }

    public static void handleServerSide(CompoundTag tag, ServerPlayer player) {
        ActiveForm activeForm = new ActiveForm(tag);
//        Avatar.LOGGER.info("Form Executed: {}", activeForm.form().name());
        NeoForge.EVENT_BUS.post(new FormActivatedEvent(activeForm, player, false));

        // Extra case for step
        if (activeForm.form().equals(STEP)) {
            tag.putBoolean("Active", false);
            ReleaseFormPacket.handleServerSide(tag, player);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
