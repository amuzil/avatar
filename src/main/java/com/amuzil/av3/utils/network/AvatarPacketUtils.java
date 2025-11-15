package com.amuzil.av3.utils.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class AvatarPacketUtils {

    public static void writeVec3(Vec3 vec, FriendlyByteBuf buffer) {
        buffer.writeDouble(vec.x);
        buffer.writeDouble(vec.y);
        buffer.writeDouble(vec.z);
    }
}
