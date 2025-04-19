package com.amuzil.omegasource.utils;

import com.amuzil.omegasource.bending.element.Element;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.Arrays;


public class AvatarCommand {
    private static final LiteralArgumentBuilder<CommandSourceStack> builder =  Commands.literal("avatar");
    // Class for registering the '/avatar' command
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        builder.then(Commands.literal("tree")
            .then(Commands.literal("reset")
        .executes(c -> {
            // Default message when no options are provided.
            InputModule.sendDebugMsg("Options: activate, form, tree");
            return 1;
        })));

        createActivateArtCommand();
        dispatcher.register(builder);
    }

    private static void createActivateArtCommand() {
        Arrays.stream(Element.Art.values()).toList().forEach(elem ->
                builder.then(Commands.literal("art")
                        .then(Commands.literal(elem.toString())
                                .executes(c -> activateElementArt(c, elem, null))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(c -> activateElementArt(c, elem, EntityArgument.getPlayer(c, "target")))
                                )
                        )
                )
        );
    }

    private static int activateElementArt(CommandContext<CommandSourceStack> ctx, Element.Art art, ServerPlayer player) throws CommandSyntaxException {
        if (player == null)
            player = ctx.getSource().getPlayerOrException();
        ServerPlayer finalPlayer = player;
        player.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
            bender.setElement(art.toString());
            CompoundTag tag = bender.serializeNBT();
            SyncBenderPacket packet = new SyncBenderPacket(tag, finalPlayer.getId());
            AvatarNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> finalPlayer), packet);
            finalPlayer.sendSystemMessage(Component.literal("Bending set to " + art));
        });
        return 1;
    }
}