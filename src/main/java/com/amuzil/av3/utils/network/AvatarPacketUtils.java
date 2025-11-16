package com.amuzil.av3.utils.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class AvatarPacketUtils {

    public static void writeVec3(Vec3 vec, FriendlyByteBuf buffer) {
        buffer.writeDouble(vec.x);
        buffer.writeDouble(vec.y);
        buffer.writeDouble(vec.z);
    }

    public static void writeDoubleArray(double[] arr, FriendlyByteBuf buffer) {
        buffer.writeInt(arr.length);
        for (double d : arr)
            buffer.writeDouble(d);
    }

    public static double[] readDoubleArray(FriendlyByteBuf buffer) {
        double[] arr = new double[buffer.readInt()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = buffer.readDouble();
        }
        return arr;
    }
}
