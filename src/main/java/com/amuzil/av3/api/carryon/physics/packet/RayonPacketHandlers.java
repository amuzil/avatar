package com.amuzil.av3.api.carryon.physics.packet;

import com.amuzil.av3.api.carryon.physics.CarryOn;
import com.amuzil.av3.api.carryon.physics.packet.impl.SendRigidBodyMovementPacket;
import com.amuzil.av3.api.carryon.physics.packet.impl.SendRigidBodyPropertiesPacket;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;


public class RayonPacketHandlers {
    private static final String VERSION = "1.0";
    public static final SimpleChannel MAIN = NetworkRegistry.newSimpleChannel(CarryOn.id("main"), () -> VERSION, VERSION::equals, VERSION::equals);

    public static void registerPackets() {
        PacketUtil.registerToClient(MAIN, SendRigidBodyMovementPacket.class);
        PacketUtil.registerToClient(MAIN, SendRigidBodyPropertiesPacket.class);
    }
}
