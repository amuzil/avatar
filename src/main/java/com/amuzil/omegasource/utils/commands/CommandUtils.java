package com.amuzil.omegasource.utils.commands;

import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.traits.SkillTrait;
import com.amuzil.omegasource.bending.BendingForm;
import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.capability.AvatarCapabilities;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.network.AvatarNetwork;
import com.amuzil.omegasource.network.packets.client.SyncBenderPacket;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;


class CommandUtils {

    static int activateElement(CommandContext<CommandSourceStack> ctx, Element element, ServerPlayer player) throws CommandSyntaxException {
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

    static int triggerForm(CommandContext<CommandSourceStack> ctx, BendingForm form, ServerPlayer player) throws CommandSyntaxException {
        if (player == null)
            player = ctx.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = player;
        
        return 1;
    }

    static int setCanUseElement(CommandContext<CommandSourceStack> ctx, Element element, boolean canUse, ServerPlayer player) throws CommandSyntaxException {
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

    static int setCanUseSkill(CommandContext<CommandSourceStack> ctx, Skill skill, boolean canUse, ServerPlayer player) throws CommandSyntaxException {
        if (player == null)
            player = ctx.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = player;
        player.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
            bender.setCanUseSkill(canUse, skill.getId());
            SyncBenderPacket packet = new SyncBenderPacket(bender.serializeNBT(), targetPlayer.getUUID());
            AvatarNetwork.sendToClient(packet, targetPlayer);
            String action = canUse
                    ? String.format("Learned %s skill", skill.name())
                    : String.format("Forgot %s skill", skill.name());
            targetPlayer.sendSystemMessage(Component.literal(action));
        });
        return 1;
    }

    static int masterElement(CommandContext<CommandSourceStack> ctx, Element element, ServerPlayer player) throws CommandSyntaxException {
        if (player == null)
            player = ctx.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = player;
        player.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
            bender.setCanUseAllSkills(element);
            SyncBenderPacket packet = new SyncBenderPacket(bender.serializeNBT(), targetPlayer.getUUID());
            AvatarNetwork.sendToClient(packet, targetPlayer);
            targetPlayer.sendSystemMessage(Component.literal("Mastered the element of " + element.nickName() + "."));
        });
        return 1;
    }

    static int setSkillTrait(CommandContext<CommandSourceStack> ctx, Skill skill, boolean canUse, ServerPlayer player, CompoundTag tag) throws CommandSyntaxException {
        if (player == null)
            player = ctx.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = player;
        player.getCapability(AvatarCapabilities.BENDER).ifPresent(bender -> {
            bender.setCanUseSkill(canUse, skill.getId());
            Bender ben = (Bender) bender;
            ben.getSkillData(skill.getId()).getSkillTraits().forEach(trait -> {
                if (trait.name().equals(tag.getString("name"))) {
                    trait.deserializeNBT(tag);
                    SyncBenderPacket packet = new SyncBenderPacket(bender.serializeNBT(), targetPlayer.getUUID());
                    AvatarNetwork.sendToClient(packet, targetPlayer);
                    targetPlayer.sendSystemMessage(Component.literal("Updated " + trait.name() + " SkillTrait for " + skill.name()));
                }
            });

        });
        return 1;
    }
}
