package com.amuzil.omegasource.utils;

import com.amuzil.omegasource.input.InputModule;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;


public class AvatarCommand {
    private static LiteralArgumentBuilder<CommandSourceStack> builder =  Commands.literal("avatar");
    // Class for registering the '/avatar' command
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        builder.then(Commands.literal("tree")
            .then(Commands.literal("reset")
        .executes(c -> {
            // Default message when no options are provided.
            InputModule.sendDebugMsg("Options: activate, form, tree");
            return 1;
        })));

        dispatcher.register(builder);
    }

}