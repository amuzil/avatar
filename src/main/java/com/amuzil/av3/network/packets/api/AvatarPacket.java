package com.amuzil.av3.network.packets.api;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public interface AvatarPacket {
    void toBytes(FriendlyByteBuf buffer);

    static AvatarPacket fromBytes(FriendlyByteBuf buffer) { return null; }

    static boolean handle(AvatarPacket packet, Supplier<NetworkEvent.Context> context) {
        return false;
    }
}
