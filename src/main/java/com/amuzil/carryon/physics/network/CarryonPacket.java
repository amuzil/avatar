package com.amuzil.carryon.physics.network;

import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;


public abstract class CarryonPacket implements CustomPacketPayload {
    protected static final Logger LOGGER = LogManager.getLogger();

    protected boolean isValid;

    public CarryonPacket(boolean valid) {
        this.isValid = valid;
    }

    public boolean isMessageValid() {
        return this.isValid;
    }

    protected abstract void toBytes(FriendlyByteBuf buffer);

    protected abstract void fromBytes(FriendlyByteBuf buffer);

    public static <T extends CarryonPacket> void encodeCheck(T packet, FriendlyByteBuf buffer) {
        if (!packet.isValid) return;
        packet.toBytes(buffer);
    }

    public static <T extends CarryonPacket> T fromBytes(Supplier<T> blank, FriendlyByteBuf buffer) {
        T message = blank.get();
        try {
            message.fromBytes(buffer);
        }
        catch (IllegalArgumentException | IndexOutOfBoundsException | DecoderException e) {
            LOGGER.warn("Exception while reading " + message.toString() + "; " + e);
            e.printStackTrace();
            return message;
        }
        message.isValid = true;
        return message;
    }

    public abstract Runnable getProcessor(IPayloadContext context);

    protected static Runnable client(IPayloadContext context, Runnable processor) {
        return () -> {
            if (context.flow().getReceptionSide().isClient())
                processor.run();
        };
    }
}
