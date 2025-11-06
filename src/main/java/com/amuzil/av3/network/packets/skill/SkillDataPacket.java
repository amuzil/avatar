package com.amuzil.av3.network.packets.skill;

import com.amuzil.magus.registry.Registries;
import com.amuzil.magus.skill.Skill;
import com.amuzil.magus.skill.data.SkillData;
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

import java.util.UUID;

public class SkillDataPacket implements AvatarPacket {
    public static final Type<SkillDataPacket> TYPE = new Type<>(Avatar.id(SkillDataPacket.class));
    public static final StreamCodec<FriendlyByteBuf, SkillDataPacket> CODEC =
            StreamCodec.ofMember(SkillDataPacket::toBytes, SkillDataPacket::new);

    private final ResourceLocation skillId;
    private final String skillUUID;
    private final SkillData skillData;

    public SkillDataPacket(ResourceLocation skillId, String skillUUID, SkillData skillData) {
        this.skillId = skillId;
        this.skillUUID = skillUUID;
        this.skillData = skillData;
    }

    public SkillDataPacket(FriendlyByteBuf buf) {
        this(null, null, null);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(skillId);
        buf.writeUUID(UUID.fromString(skillUUID));
        buf.writeNbt(skillData.serializeNBT(null));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClientSide(ResourceLocation skillId, String skillUUID, SkillData skillData) {
        Player player = Minecraft.getInstance().player;
        assert player != null;
        Bender bender = (Bender) Bender.getBender(player);
        Skill skill = Registries.getSkill(skillId);
        assert skill != null;
        assert bender != null;
        bender.getSkillData(skillUUID);
    }

    public static void handle(SkillDataPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.flow().getReceptionSide().isClient()) {
//                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClientSide(msg.skillId, msg.skillUUID, msg.skillData));
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
