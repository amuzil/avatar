package com.amuzil.av3.network.packets.skill;

import com.amuzil.magus.registry.Registries;
import com.amuzil.magus.skill.Skill;
import com.amuzil.av3.capability.Bender;
import com.amuzil.av3.network.packets.api.AvatarPacket;
import com.amuzil.av3.Avatar;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ActivatedSkillPacket implements AvatarPacket {
    public static final Type<ActivatedSkillPacket> TYPE = new Type<>(Avatar.id(ActivatedSkillPacket.class));
    public static final StreamCodec<FriendlyByteBuf, ActivatedSkillPacket> CODEC =
            StreamCodec.ofMember(ActivatedSkillPacket::toBytes, ActivatedSkillPacket::new);

    private final ResourceLocation skillId;
    private final int skillState; // SkillState (START, RUN, STOP, IDLE)

    public ActivatedSkillPacket(ResourceLocation skillId, int skillState) {
        this.skillId = skillId;
        this.skillState = skillState;
    }

    public ActivatedSkillPacket(FriendlyByteBuf buf) {
        this.skillId = buf.readResourceLocation();
        this.skillState = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(skillId);
        buf.writeInt(skillState);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClientSide(ResourceLocation skillId, int skillState) {
        Player player = Minecraft.getInstance().player;
        assert player != null;
        Bender bender = (Bender) Bender.getBender(player);
//        Skill skill = Registries.SKILLS.get().getValue(skillId);
        Skill skill = Registries.getSkill(skillId);
        assert skill != null;
        Skill.SkillState newSkillState = Skill.SkillState.values()[skillState];
        // If you want this to work, pass SkillUUID instead.
//        System.out.println("DEBUG: ActivatedSkillPacket " + bender.getSkillData(skill) + " "  + newSkillState);
        bender.getSkillData(skill).setSkillState(newSkillState); // Sync SkillState to client
        switch (newSkillState) {
            case START -> skill.start(bender);
            case RUN -> skill.run(bender);
            case STOP -> skill.stop(bender);
        }
    }

    public static void handle(ActivatedSkillPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.flow().getReceptionSide().isClient())
                handleClientSide(msg.skillId, msg.skillState);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
