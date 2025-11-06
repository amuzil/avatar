package com.amuzil.carryon.physics.network;

import com.google.common.collect.Maps;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;


public class PacketUtil {
    private static @Nullable Throwable lastException;
    private static final Map<PayloadRegistrar, AtomicInteger> CURRENT_IDS = Maps.newConcurrentMap();
    private static final Logger LOGGER = LogManager.getLogger();

    public static <T extends CarryonPacket> void registerToClient(PayloadRegistrar registrar, Class<T> clazz) {
        registrar.playToClient(
                CURRENT_IDS.computeIfAbsent(registrar, c -> new AtomicInteger()).incrementAndGet(),
                clazz,
                CarryonPacket::encodeCheck,
                buffer -> CarryonPacket.fromBytes(() -> {
                    try {
                        return clazz.getDeclaredConstructor().newInstance();
                    }
                    catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                        LOGGER.error("Failed to create blank packet from class {}", clazz);
                        e.printStackTrace();
                        return null;
                    }
                }, buffer),
                PacketUtil::receiveClientMessage,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
    }

    public static <T extends CarryonPacket> void registerToServer(PayloadRegistrar registrar, Class<T> clazz) {
        registrar.registerMessage(
                CURRENT_IDS.computeIfAbsent(registrar, c -> new AtomicInteger()).incrementAndGet(),
                clazz,
                CarryonPacket::encodeCheck,
                buffer -> CarryonPacket.fromBytes(() -> {
                    try {
                        return clazz.getDeclaredConstructor().newInstance();
                    }
                    catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                        LOGGER.error("Failed to create blank packet from class {}", clazz);
                        e.printStackTrace();
                        return null;
                    }
                }, buffer),
                PacketUtil::receiveServerMessage,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
    }

    private static <T extends CarryonPacket> void receiveClientMessage(final T message, Supplier<IPayloadContext> supplier) {
        IPayloadContext context = supplier.get();
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

    private static <T extends CarryonPacket> void receiveServerMessage(final T message, Supplier<IPayloadContext> supplier) {
        IPayloadContext context = supplier.get();
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
