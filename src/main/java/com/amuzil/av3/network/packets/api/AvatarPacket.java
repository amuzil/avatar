package com.amuzil.av3.network.packets.api;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface AvatarPacket extends CustomPacketPayload {
    void toBytes(FriendlyByteBuf buffer);
}
