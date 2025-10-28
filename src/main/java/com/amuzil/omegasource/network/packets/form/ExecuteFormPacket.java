package com.amuzil.omegasource.network.packets.form;

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

import static com.amuzil.omegasource.bending.form.BendingForms.STEP;


public class ExecuteFormPacket implements AvatarPacket {
    private final CompoundTag tag;

    public ExecuteFormPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public static void handleServerSide(CompoundTag tag, ServerPlayer player) {
        assert player != null;
        ServerLevel level = player.serverLevel();
        ActiveForm activeForm = new ActiveForm(tag);
//        Avatar.LOGGER.info("Form Executed: {}", activeForm.form().name());

        MinecraftForge.EVENT_BUS.post(new FormActivatedEvent(activeForm, player, false));

        // Extra case for step
        if (activeForm.form().equals(STEP)) {
            tag.putBoolean("Active", false);
            ReleaseFormPacket.handleServerSide(tag, player);
        }
    }

    public static void handle(ExecuteFormPacket msg, Supplier<NetworkEvent.Context> ctx) {
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

    public static ExecuteFormPacket fromBytes(FriendlyByteBuf buffer) {
        return new ExecuteFormPacket(new ActiveForm(buffer.readNbt()).serializeNBT());
    }
}
