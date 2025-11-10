package com.amuzil.av3.utils.commands;

import com.amuzil.av3.bending.element.Element;
import com.amuzil.av3.bending.form.BendingForm;
import com.amuzil.av3.data.capability.AvatarCapabilities;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.data.capability.IBender;
import com.amuzil.av3.network.packets.form.ExecuteFormPacket;
import com.amuzil.av3.network.packets.form.ReleaseFormPacket;
import com.amuzil.magus.form.ActiveForm;
import com.amuzil.magus.form.Form;
import com.amuzil.magus.skill.Skill;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;


class CommandUtils {

    static int activateElement(CommandContext<CommandSourceStack> ctx, Element element, ServerPlayer player) throws CommandSyntaxException {
        if (player == null)
            player = ctx.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = player;
        IBender bender = player.getCapability(AvatarCapabilities.BENDER);
        if(bender  != null) {
            bender.setElement(element);
            bender.syncToClient();
            targetPlayer.sendSystemMessage(Component.literal("Active Bending set to " + element.name()));
        }
        return 1;
    }

    static int triggerForm(CommandContext<CommandSourceStack> ctx, Form form, ServerPlayer player) throws CommandSyntaxException {
        if (player == null)
            player = ctx.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = player;
        BendingForm bendingForm = (BendingForm) form;
        ExecuteFormPacket.handleServerSide(new ActiveForm(bendingForm, true).serializeNBT(), targetPlayer);
        ReleaseFormPacket.handleServerSide(new ActiveForm(bendingForm, false).serializeNBT(), targetPlayer);
        return 1;
    }

    static int triggerSkill(CommandContext<CommandSourceStack> ctx, Skill skill, Skill.SkillState state, ServerPlayer player) throws CommandSyntaxException {
        if (player == null)
            player = ctx.getSource().getPlayerOrException();
        IBender bender = player.getCapability(AvatarCapabilities.BENDER);
        if(bender  != null)
        {
            Skill newSkill = skill.create((Bender) bender);
            switch (state) {
                case START -> newSkill.start((Bender) bender);
                case RUN -> newSkill.run((Bender) bender);
                case STOP -> {
                    for (Skill activeSkill: ((Bender) bender).activeSkills.values().stream().toList())
                        if (skill.name().equals(activeSkill.name()))
                            activeSkill.stop((Bender) bender);
                }
            }
        }
        return 1;
    }

    static int resetSkill(CommandContext<CommandSourceStack> ctx, Skill skill, ServerPlayer player) throws CommandSyntaxException {
        if (player == null)
            player = ctx.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = player;
        IBender bender = player.getCapability(AvatarCapabilities.BENDER);
        if(bender  != null)
        {
            String action;
            if (skill != null) {
                bender.resetSkillData(skill);
                action = String.format("Resetting %s skill", skill.name());
            } else {
                bender.resetSkillData();
                action = "Resetting all skills";
            }
            bender.syncToClient();
            targetPlayer.sendSystemMessage(Component.literal(action));
        }
        return 1;
    }

    static int setCanUseElement(CommandContext<CommandSourceStack> ctx, Element element, boolean canUse, ServerPlayer player) throws CommandSyntaxException {
        if (player == null)
            player = ctx.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = player;
        IBender bender = player.getCapability(AvatarCapabilities.BENDER);
        if(bender == null) return 1;
        bender.setCanUseElement(canUse, element);
        bender.syncToClient();
        String action = canUse
                ? String.format("Granted the power of %s. May the element of %s protect you.", element.name(), element.nickName())
                : String.format("Taken %s away.", element.name());
        targetPlayer.sendSystemMessage(Component.literal(action));
        return 1;
    }

    static int setCanUseSkill(CommandContext<CommandSourceStack> ctx, Skill skill, boolean canUse, ServerPlayer player) throws CommandSyntaxException {
        if (player == null)
            player = ctx.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = player;
        IBender bender = player.getCapability(AvatarCapabilities.BENDER);
        if(bender == null) return 1;
        bender.setCanUseSkill(canUse, skill.name());
        bender.syncToClient();
        String action = canUse
                ? String.format("Learned %s skill", skill.name())
                : String.format("Forgot %s skill", skill.name());
        targetPlayer.sendSystemMessage(Component.literal(action));
        return 1;
    }

    static int masterElement(CommandContext<CommandSourceStack> ctx, Element element, ServerPlayer player) throws CommandSyntaxException {
        if (player == null)
            player = ctx.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = player;
        IBender bender = player.getCapability(AvatarCapabilities.BENDER);
        if(bender == null) return 1;
        bender.setCanUseAllSkills(element);
        bender.syncToClient();
        targetPlayer.sendSystemMessage(Component.literal("Mastered the element of " + element.nickName() + "."));

        return 1;
    }

    static int setSkillTrait(CommandContext<CommandSourceStack> ctx, Skill skill, boolean canUse, ServerPlayer player, CompoundTag tag) throws CommandSyntaxException {
        if (player == null)
            player = ctx.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = player;
        IBender bender = player.getCapability(AvatarCapabilities.BENDER);
        if(bender == null) return 1;
        bender.setCanUseSkill(canUse, skill.name());
        Bender ben = (Bender) bender;
        ben.getSkillData(skill.name()).getSkillTraits().forEach(trait -> {
            if (trait.name().equals(tag.getString("name"))) {
                trait.deserializeNBT(targetPlayer.level().registryAccess(), tag);
                bender.syncToClient();
                targetPlayer.sendSystemMessage(Component.literal("Updated " + trait.name() + " SkillTrait for " + skill.name()));
            }
        });
        return 1;
    }
}
