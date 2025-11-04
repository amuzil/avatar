package com.amuzil.av3.network.packets.api;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Supplier;

public interface AvatarPacket extends CustomPacketPayload {
    void toBytes(FriendlyByteBuf buffer);
}
