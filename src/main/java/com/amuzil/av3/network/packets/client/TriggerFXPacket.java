package com.amuzil.av3.network.packets.client;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.modules.client.SoundModule;
import com.amuzil.av3.network.packets.api.AvatarPacket;
import com.amuzil.caliber.physics.mixin.common.LevelMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public class TriggerFXPacket implements AvatarPacket {
    public static final Type<TriggerFXPacket> TYPE = new Type<>(Avatar.id(TriggerFXPacket.class));
    public static final StreamCodec<FriendlyByteBuf, TriggerFXPacket> STREAM_CODEC =
            StreamCodec.ofMember(TriggerFXPacket::toBytes, TriggerFXPacket::new);

    private final ResourceLocation fxId;
    private final int entityId;

    public TriggerFXPacket(ResourceLocation fxId, int entityId) {
        this.fxId = fxId;
        this.entityId = entityId;
    }

    public TriggerFXPacket(FriendlyByteBuf buf) {
        this.fxId = buf.readResourceLocation();
        this.entityId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(fxId);
        buf.writeInt(entityId);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClientSide(ResourceLocation fxId, int entityId) {
        Level level = Minecraft.getInstance().level;
        assert level != null;
        if (!(level.getEntity(entityId) instanceof AvatarEntity entity)) return;
        SoundModule.startSoundEffect(fxId, entity);
    }

    public static void handle(TriggerFXPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.flow().getReceptionSide().isClient())
                handleClientSide(msg.fxId, msg.entityId);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
