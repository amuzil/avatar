package com.amuzil.av3.network.packets.skill;

import com.amuzil.magus.registry.Registries;
import com.amuzil.magus.skill.Skill;
import com.amuzil.magus.skill.data.SkillData;
import com.amuzil.av3.capability.Bender;
import com.amuzil.av3.network.packets.api.AvatarPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;


public class SkillDataPacket implements AvatarPacket {

    private final ResourceLocation skillId;
    private final String skillUUID;
    private final SkillData skillData;

    public SkillDataPacket(ResourceLocation skillId, String skillUUID, SkillData skillData) {
        this.skillId = skillId;
        this.skillUUID = skillUUID;
        this.skillData = skillData;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(skillId);
        buf.writeUUID(UUID.fromString(skillUUID));
        buf.writeNbt(skillData.serializeNBT());
    }

    public static SkillDataPacket fromBytes(FriendlyByteBuf buf) {
//        Skill clientSkill
//        return new SkillDataPacket(buf.readResourceLocation(), buf.readUUID().toString(), new SkillData().buf.readInt());
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClientSide(ResourceLocation skillId, String skillUUID, SkillData skillData) {
        Player player = Minecraft.getInstance().player;
        assert player != null;
        Bender bender = (Bender) Bender.getBender(player);
        Skill skill = Registries.SKILLS.get().getValue(skillId);
        assert skill != null;
        bender.getSkillData(skillUUID);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
//                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClientSide(skillId, skillUUID, skillState));
            }
        });
        return true;
    }
}
