package com.amuzil.omegasource.utils;

import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.capability.AvatarCapabilities;
import com.amuzil.omegasource.input.InputModule;
import com.amuzil.omegasource.network.AvatarNetwork;
import com.amuzil.omegasource.network.packets.client.SyncBenderPacket;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;


public class AvatarCommand {
    // Class for registering the '/avatar' command
    private static final LiteralArgumentBuilder<CommandSourceStack> builder =  Commands.literal("avatar");
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        builder.then(Commands.literal("tree")
                .then(Commands.literal("reset")
                        .executes(c -> {
                            // Default message when no options are provided.
                            InputModule.sendDebugMsg("Options: activate, form, tree");
                            return 1;
                        })));
        activateElementCommand();
        // TODO -> Add the following commands:
        //  - Add activate Skill command
        //  - Add activate Form command
        //  - Add setCanUse Element command
        dispatcher.register(builder);
    }

    // For testing
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
}