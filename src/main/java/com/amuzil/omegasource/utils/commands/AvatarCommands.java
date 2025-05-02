package com.amuzil.omegasource.utils.commands;

import com.amuzil.omegasource.api.magus.form.Form;
import com.amuzil.omegasource.api.magus.registry.Registries;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.bending.BendingForm;
import com.amuzil.omegasource.bending.BendingForms;
import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.input.InputModule;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import static com.amuzil.omegasource.utils.commands.CommandUtils.*;


public class AvatarCommands {
    // Class for registering the '/avatar' commands
    private static final LiteralArgumentBuilder<CommandSourceStack> builder =  Commands.literal("avatar");

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        builder.then(Commands.literal("tree")
                .then(Commands.literal("reset")
                        .executes(c -> {
                            // Default message when no args are provided.
                            InputModule.sendDebugMsg("Options: activate, grant, take, element");
                            return 1;
                        })));
        createSkillCommands();
        createElementCommands();
        createMasterCommands();
        createFormCommands();
//        createElementCommand("activate", activateElementCommand);
//        createElementCommand("grant", grantElementCommand);
//        createElementCommand("take", takeElementCommand);
        // TODO -> Add the following commands:
        //  - Add set SkillTrait command
        //  - Add trigger Form command
        //  - Add trigger Skill command
        dispatcher.register(builder);
    }

    private static void createElementCommands() {
        for (Element elem : Elements.ALL_FOUR.values()) {
            builder.then(activateElementCommand(elem));
            builder.then(setCanUseElementCommand(elem, "grant"));
            builder.then(setCanUseElementCommand(elem, "take"));
        }
    }

    private static void createSkillCommands() {
        for (Skill skill : Registries.getSkills()) {
            builder.then(setCanUseSkillCommand(skill, "grant"));
            builder.then(setCanUseSkillCommand(skill, "take"));
            builder.then(triggerSkillCommand(skill, "start"));
            builder.then(triggerSkillCommand(skill, "run"));
            builder.then(triggerSkillCommand(skill, "stop"));
        }
    }

    private static void createMasterCommands() {
        for (Element elem : Elements.ALL_FOUR.values()) {
            builder.then(masterElementCommand(elem));
        }
    }

    private static void createFormCommands() {
        for (Form form : Registries.getForms()) {
            builder.then(triggerFormCommand(form));
        }
    }

    private static LiteralArgumentBuilder<CommandSourceStack> activateElementCommand(Element elem) {
        return Commands.literal("activate")
                .then(Commands.literal(elem.nickName())
                        .executes(c -> activateElement(c, elem, null))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(c -> activateElement(c, elem, EntityArgument.getPlayer(c, "target")))
                        )
                );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> setCanUseElementCommand(Element elem, String action) {
        boolean canUse = action.equals("grant");
        return Commands.literal(action).then(Commands.literal("element")
                .then(Commands.literal(elem.nickName())
                        .executes(c -> setCanUseElement(c, elem, canUse, null))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(c -> setCanUseElement(c, elem, canUse, EntityArgument.getPlayer(c, "target")))
                        )
                ));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> setCanUseSkillCommand(Skill skill, String action) {
        boolean canUse = action.equals("grant");
        return Commands.literal(action).then(Commands.literal("skill")
                .then(Commands.literal(skill.name())
                        .executes(c -> setCanUseSkill(c, skill, canUse, null))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(c -> setCanUseSkill(c, skill, canUse, EntityArgument.getPlayer(c, "target")))
                                .then(Commands.argument("nbt", CompoundTagArgument.compoundTag())
                                        .executes(c -> setSkillTrait(c, skill, canUse, EntityArgument.getPlayer(c, "target"), CompoundTagArgument.getCompoundTag(c, "nbt")))
                                )
                        )
                ));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> masterElementCommand(Element elem) {
        return Commands.literal("master")
                .then(Commands.literal(elem.nickName())
                        .executes(c -> masterElement(c, elem, null))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(c -> masterElement(c, elem, EntityArgument.getPlayer(c, "target")))
                        )
                );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> triggerFormCommand(Form form) {
        return Commands.literal("trigger").then(Commands.literal("form")
                .then(Commands.literal(form.name())
                        .executes(c -> triggerForm(c, form, null))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(c -> triggerForm(c, form, EntityArgument.getPlayer(c, "target")))
                        )
                ));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> triggerSkillCommand(Skill skill, String action) {
        return Commands.literal("trigger").then(Commands.literal("skill").then(Commands.literal(action)
                .then(Commands.literal(skill.name())
                        .executes(c -> triggerSkill(c, skill, action, null))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(c -> triggerSkill(c, skill, action, EntityArgument.getPlayer(c, "target")))
                        )
                )));
    }

//        private static void createElementCommand(String action, Command<CommandSourceStack> subCommand) {
//        boolean canUse = action.equals("grant");
//        LiteralArgumentBuilder<CommandSourceStack> elementCommand = Commands.literal(action);
//        Elements.ALL_FOUR.values().forEach(elem -> {
//            elementCommand.then(Commands.literal(elem.nickName())
//                    .executes(subCommand)
//                    .then(Commands.argument("target", EntityArgument.player())
//                            .executes(subCommand)
//                    )
//            );
//        });
//        builder.then(elementCommand);
//    }

//    private static final Command<CommandSourceStack> activateElementCommand = c -> {
//        String elemName = c.getNodes().get(2).getNode().getName();
//        Element element = Elements.ALL_FOUR.get(elemName);
//        boolean hasTarget = c.getNodes().stream()
//                .anyMatch(node -> "target".equals(node.getNode().getName()));
//        ServerPlayer target = hasTarget ? EntityArgument.getPlayer(c, "target") : null;
//        return activateElement(c, element, target);
//    };

}