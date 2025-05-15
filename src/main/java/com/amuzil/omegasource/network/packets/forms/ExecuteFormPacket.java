package com.amuzil.omegasource.network.packets.forms;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.bending.BendingSelection;
import com.amuzil.omegasource.capability.AvatarCapabilities;
import com.amuzil.omegasource.events.FormActivatedEvent;
import com.amuzil.omegasource.network.packets.api.AvatarPacket;
import kotlin.OptIn;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.valkyrienskies.core.api.VSBeta;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.api.world.ServerShipWorld;
import org.valkyrienskies.core.apigame.ShipTeleportData;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.command.RelativeValue;
import org.valkyrienskies.mod.common.command.RelativeVector3;
import org.valkyrienskies.mod.common.util.GameTickForceApplier;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.util.RelocationUtilKt;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static com.amuzil.omegasource.bending.form.BendingForms.*;


public class ExecuteFormPacket implements AvatarPacket {
    private final CompoundTag tag;

    public ExecuteFormPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public static void handleServerSide(CompoundTag tag, ServerPlayer player) {
        // Work that needs to be thread-safe (most work)
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
