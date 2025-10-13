package com.amuzil.omegasource.network.packets.skill;

import com.amuzil.omegasource.api.magus.registry.Registries;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.network.packets.api.AvatarPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;


public class SkillDataPacket implements AvatarPacket {

    private final ResourceLocation skillId;
    private final String skillUuId;
    private final SkillData skillData;

    public SkillDataPacket(ResourceLocation skillId, String skillUuId, SkillData skillData) {
        this.skillId = skillId;
        this.skillUuId = skillUuId;
        this.skillData = skillData;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(skillId);
        buf.writeUUID(UUID.fromString(skillUuId));
        buf.writeNbt(skillData.serializeNBT());
    }

    public static SkillDataPacket fromBytes(FriendlyByteBuf buf) {
//        Skill clientSkill
//        return new SkillDataPacket(buf.readResourceLocation(), buf.readUUID().toString(), new SkillData().buf.readInt());
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClientSide(ResourceLocation skillId, String skillUuId, SkillData skillData) {
        Player player = Minecraft.getInstance().player;
        assert player != null;
        Bender bender = (Bender) Bender.getBender(player);
        Skill skill = Registries.SKILLS.get().getValue(skillId);
        assert skill != null;
        bender.getSkillData(skillUuId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
//                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClientSide(skillId, skillUuId, skillState));
            }
        });
        return true;
    }
}
