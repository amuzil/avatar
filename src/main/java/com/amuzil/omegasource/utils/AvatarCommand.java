package com.amuzil.omegasource.utils;

import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.capability.AvatarCapabilities;
import com.amuzil.omegasource.input.InputModule;
import com.amuzil.omegasource.network.AvatarNetwork;
import com.amuzil.omegasource.network.packets.client.SyncBenderPacket;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;


public class AvatarCommand {
    // Class for registering the '/avatar' command
    private static final LiteralArgumentBuilder<CommandSourceStack> builder =  Commands.literal("avatar");
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        builder.then(Commands.literal("tree")
                .then(Commands.literal("reset")
                        .executes(c -> {
                            // Default message when no args are provided.
                            InputModule.sendDebugMsg("Options: activate, grant, take, element");
                            return 1;
                        })));
        createElementCommand("activate", activateElementCommand);
        createElementCommand("grant", grantElementCommand);
        createElementCommand("take", takeElementCommand);
//        grantElementCommand();
        // TODO -> Add the following commands:
        //  - Add activate Form command
        //  - Add activate Skill command
        dispatcher.register(builder);
    }

    private static void createElementCommand(String arg, Command<CommandSourceStack> subCommand) {
        LiteralArgumentBuilder<CommandSourceStack> elementCommand = Commands.literal(arg);
        Elements.ALL_FOUR.values().forEach(elem -> {
            elementCommand.then(Commands.literal(elem.nickName())
                    .executes(subCommand)
                    .then(Commands.argument("target", EntityArgument.player())
                            .executes(subCommand)
                    )
            );
        });
        builder.then(elementCommand);
    }

    private static final Command<CommandSourceStack> activateElementCommand = c -> {
        String elemName = c.getNodes().get(2).getNode().getName();
        Element element = Elements.ALL_FOUR.get(elemName);
        boolean hasTarget = c.getNodes().stream()
                .anyMatch(node -> "target".equals(node.getNode().getName()));
        ServerPlayer target = hasTarget ? EntityArgument.getPlayer(c, "target") : null;
        return activateElement(c, element, target);
    };

    private static final Command<CommandSourceStack> grantElementCommand = c -> {
        String elemName = c.getNodes().get(2).getNode().getName();
        Element element = Elements.ALL_FOUR.get(elemName);
        boolean hasTarget = c.getNodes().stream()
                .anyMatch(node -> "target".equals(node.getNode().getName()));
        ServerPlayer target = hasTarget ? EntityArgument.getPlayer(c, "target") : null;
        return setCanUseElement(c, element, true, target);
    };

    private static final Command<CommandSourceStack> takeElementCommand = c -> {
        String elemName = c.getNodes().get(2).getNode().getName();
        Element element = Elements.ALL_FOUR.get(elemName);
        boolean hasTarget = c.getNodes().stream()
                .anyMatch(node -> "target".equals(node.getNode().getName()));
        ServerPlayer target = hasTarget ? EntityArgument.getPlayer(c, "target") : null;
        return setCanUseElement(c, element, false, target);
    };

    private static void activateElementCommand() {
        Elements.ALL_FOUR.values().forEach(elem ->
                builder.then(Commands.literal("element")
                        .then(Commands.literal(elem.nickName())
                                .executes(c -> activateElement(c, elem, null))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(c -> activateElement(c, elem, EntityArgument.getPlayer(c, "target")))
                                )
                        )
                )
        );
    }

    private static void grantElementCommand() {
        Elements.ALL_FOUR.values().forEach(elem ->
                builder.then(Commands.literal("grant")
                        .then(Commands.literal(elem.nickName())
                                .executes(c -> setCanUseElement(c, elem, true,null))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(c -> setCanUseElement(c, elem, true, EntityArgument.getPlayer(c, "target")))
                                )
                        )
                )
        );
    }



    private static int activateElement(CommandContext<CommandSourceStack> ctx, Element element, ServerPlayer player) throws CommandSyntaxException {
        if (player == null)
            player = ctx.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = player;
        player.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
            bender.setElement(element);
            SyncBenderPacket packet = new SyncBenderPacket(bender.serializeNBT(), targetPlayer.getUUID());
            AvatarNetwork.sendToClient(packet, targetPlayer);
            targetPlayer.sendSystemMessage(Component.literal("Active Bending set to " + element.name()));
        });
        return 1;
    }

    private static int setCanUseElement(CommandContext<CommandSourceStack> ctx, Element element, boolean canUse, ServerPlayer player) throws CommandSyntaxException {
        if (player == null)
            player = ctx.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = player;
        player.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
            bender.setCanUseElement(canUse, element);
            SyncBenderPacket packet = new SyncBenderPacket(bender.serializeNBT(), targetPlayer.getUUID());
            AvatarNetwork.sendToClient(packet, targetPlayer);
            String action = canUse
                    ? String.format("Granted the power of %s. May the element of %s protect you.", element.name(), element.nickName())
                    : String.format("Taken %s away.", element.name());
            targetPlayer.sendSystemMessage(Component.literal(action));
        });
        return 1;
    }
}