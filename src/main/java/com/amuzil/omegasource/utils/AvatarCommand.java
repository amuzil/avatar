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

        createActivateArtCommand();
        // TODO -> Add the following commands:
        //  - Add activate Skill command
        //  - Add activate Form command
        //  - Add setCanUse Element command
        dispatcher.register(builder);
    }

    private static void createActivateArtCommand() {
//        Arrays.stream(Element.Art.values()).toList().forEach(elem ->
//                builder.then(Commands.literal("art")
//                        .then(Commands.literal(elem.toString())
//                                .executes(c -> activateElementArt(c, elem, null))
//                                .then(Commands.argument("target", EntityArgument.player())
//                                        .executes(c -> activateElementArt(c, elem, EntityArgument.getPlayer(c, "target")))
//                                )
//                        )
//                )
//        );
    }

//    private static int activateElementArt(CommandContext<CommandSourceStack> ctx, Element.Art art, ServerPlayer player) throws CommandSyntaxException {
//        if (player == null)
//            player = ctx.getSource().getPlayerOrException();
//        ServerPlayer targetPlayer = player;
//        player.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
//            bender.setElement(Elements.get(art));
//            SyncBenderPacket packet = new SyncBenderPacket(bender.serializeNBT(), targetPlayer.getUUID());
//            AvatarNetwork.sendToClient(packet, targetPlayer);
//            targetPlayer.sendSystemMessage(Component.literal("Bending set to " + art));
//        });
//        return 1;
//    }
}