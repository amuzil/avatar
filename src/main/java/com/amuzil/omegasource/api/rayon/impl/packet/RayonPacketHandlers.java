package com.amuzil.omegasource.api.rayon.impl.packet;

import com.amuzil.omegasource.api.rayon.impl.Rayon;
import com.amuzil.omegasource.api.rayon.impl.packet.impl.SendRigidBodyMovementPacket;
import com.amuzil.omegasource.api.rayon.impl.packet.impl.SendRigidBodyPropertiesPacket;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import nonamecrackers2.crackerslib.common.packet.PacketUtil;

public class RayonPacketHandlers
{
	private static final String VERSION = "1.0";
	public static final SimpleChannel MAIN = NetworkRegistry.newSimpleChannel(Rayon.id("main"), () -> VERSION, VERSION::equals, VERSION::equals);
	
	public static void registerPackets() {
		PacketUtil.registerToClient(MAIN, SendRigidBodyMovementPacket.class);
		PacketUtil.registerToClient(MAIN, SendRigidBodyPropertiesPacket.class);
	}
}
