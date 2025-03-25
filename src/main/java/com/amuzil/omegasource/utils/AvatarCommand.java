package com.amuzil.omegasource.utils;

import com.amuzil.omegasource.input.DefaultInputModule;
import com.amuzil.omegasource.registry.Registries;
import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.bending.form.Form;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.Arrays;

import static com.amuzil.omegasource.Avatar.MOD_ID;


public class AvatarCommand {
    private static LiteralArgumentBuilder<CommandSourceStack> builder =  Commands.literal("avatar");
    // Class for registering the '/avatar' command
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        builder.then(Commands.literal("tree")
            .then(Commands.literal("reset")
        .executes(c -> {
            // Default message when no options are provided.
            DefaultInputModule.sendDebugMsg("Options: activate, form, tree");
            return 1;
        })));

        dispatcher.register(builder);
    }

}