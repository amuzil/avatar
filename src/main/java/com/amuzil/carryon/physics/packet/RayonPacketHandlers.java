package com.amuzil.carryon.physics.packet;

import com.amuzil.carryon.CarryOn;
import com.amuzil.carryon.physics.packet.impl.SendRigidBodyMovementPacket;
import com.amuzil.carryon.physics.packet.impl.SendRigidBodyPropertiesPacket;
import net.neoforged.neoforge.network.registration.NetworkRegistry;


public class RayonPacketHandlers {
    private static final String VERSION = "1.0.0";
    public static final SimpleChannel MAIN = NetworkRegistry.newSimpleChannel(CarryOn.id("main"), () -> VERSION, VERSION::equals, VERSION::equals);

    public static void registerPackets() {
        PacketUtil.registerToClient(MAIN, SendRigidBodyMovementPacket.class);
        PacketUtil.registerToClient(MAIN, SendRigidBodyPropertiesPacket.class);
    }
}
