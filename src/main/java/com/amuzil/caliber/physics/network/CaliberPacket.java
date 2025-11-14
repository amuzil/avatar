package com.amuzil.caliber.physics.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class CaliberPacket implements CustomPacketPayload {
    protected static final Logger LOGGER = LogManager.getLogger();

    protected boolean isValid;

    public CaliberPacket(boolean valid) {
        this.isValid = valid;
    }

    public boolean isMessageValid() {
        return this.isValid;
    }

    protected abstract void toBytes(FriendlyByteBuf buffer);

//    protected abstract void fromBytes(FriendlyByteBuf buffer);

    public static <T extends CaliberPacket> void encodeCheck(T packet, FriendlyByteBuf buffer) {
        if (!packet.isValid) return;
        packet.toBytes(buffer);
    }

    public abstract Runnable getProcessor(IPayloadContext context);

    protected static Runnable client(IPayloadContext context, Runnable processor) {
        return () -> {
            if (context.flow().getReceptionSide().isClient())
                processor.run();
        };
    }
}
