package com.amuzil.av3.network.packets.skill;

import com.amuzil.magus.registry.Registries;
import com.amuzil.magus.skill.Skill;
import com.amuzil.av3.capability.Bender;
import com.amuzil.av3.network.packets.api.AvatarPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Supplier;


public class ActivatedSkillPacket implements AvatarPacket {

    private final ResourceLocation skillId;
    private final int skillState; // SkillState (START, RUN, STOP, IDLE)

    public ActivatedSkillPacket(ResourceLocation skillId, int skillState) {
        this.skillId = skillId;
        this.skillState = skillState;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(skillId);
        buf.writeInt(skillState);
    }

    public static ActivatedSkillPacket fromBytes(FriendlyByteBuf buf) {
        return new ActivatedSkillPacket(buf.readResourceLocation(), buf.readInt());
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

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClientSide(skillId, skillState));
            }
        });
        return true;
    }
}
