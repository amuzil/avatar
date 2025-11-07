package com.amuzil.carryon.physics.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;


public class PacketUtil {
    private static @Nullable Throwable lastException;
    private static final Logger LOGGER = LogManager.getLogger();

    static <T extends CarryonPacket> void receiveClientMessage(final T message, IPayloadContext context) {
        LogicalSide sideReceived = context.flow().getReceptionSide();

        if (sideReceived != LogicalSide.CLIENT) {
            LOGGER.warn(message.toString() + " was received on the wrong side: " + sideReceived);
            return;
        }

        if (!message.isMessageValid()) {
            LOGGER.warn(message.toString() + " was invalid");
            return;
        }

        context.enqueueWork(message.getProcessor(context)).handle((v, e) -> {
            if (e != null) {
                if (lastException == null || !lastException.getClass().equals(e.getClass())) {
                    LOGGER.error("Failed to process packet {}: {}", message, e);
                    e.printStackTrace();
                }
                lastException = e;
            }
            return v;
        });
    }

    static <T extends CarryonPacket> void receiveServerMessage(final T message, IPayloadContext context) {
        LogicalSide sideReceived = context.flow().getReceptionSide();

        if (sideReceived != LogicalSide.SERVER) {
            LOGGER.warn(message.toString() + " was received on the wrong side: " + sideReceived);
            return;
        }

        if (!message.isMessageValid()) {
            LOGGER.warn(message.toString() + " was invalid");
            return;
        }

        final ServerPlayer player = (ServerPlayer) context.player();
        if (player == null) {
            LOGGER.warn("The sending player is not present when " + message.toString() + " was received");
            return;
        }

        context.enqueueWork(message.getProcessor(context)).handle((v, e) -> {
            if (e != null) {
                if (lastException == null || !lastException.getClass().equals(e.getClass())) {
                    LOGGER.error("Failed to process packet {}: {}", message, e);
                    e.printStackTrace();
                }
                lastException = e;
            }
            return v;
        });
    }
}
