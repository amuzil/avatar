package com.amuzil.omegasource.network.packets.skill;

import com.amuzil.omegasource.api.magus.registry.Registries;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.entity.AvatarProjectile;
import com.amuzil.omegasource.network.AvatarNetwork;
import com.amuzil.omegasource.network.packets.api.AvatarPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.amuzil.omegasource.bending.form.BendingForms.STRIKE;


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

    // Server-side handler
    public static void handleServerSide(ResourceLocation skillId, int skillState, ServerPlayer player) {
        // Perform server-side entity spawning and updating logic and fire Form Event here
        ServerLevel level = player.serverLevel();
        ActivatedSkillPacket packet = new ActivatedSkillPacket(skillId, skillState);
        Predicate<ServerPlayer> predicate = (serverPlayer) -> player.distanceToSqr(serverPlayer) < 2500;
        for (ServerPlayer nearbyPlayer: level.getPlayers(predicate.and(LivingEntity::isAlive))) {
            AvatarNetwork.sendToClient(packet, nearbyPlayer);
        }
    }

    // Client-side handler
    @OnlyIn(Dist.CLIENT)
    private static void handleClientSide(ResourceLocation skillId, int skillState) {
        // Perform client-side particle effect or other rendering logic here
        Player player = Minecraft.getInstance().player;
        assert player != null;
        Bender bender = (Bender) Bender.getBender(player);
        Skill skill = Registries.SKILLS.get().getValue(skillId);
        assert skill != null;
        switch (Skill.SkillState.values()[skillState]) {
            case START -> skill.start(bender);
            case RUN -> skill.run(bender);
            case STOP -> skill.stop(bender);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClientSide(skillId, skillState));
            } else {
                ServerPlayer player = ctx.get().getSender();
                assert player != null;
                handleServerSide(skillId, skillState, player);
            }
        });
        return true;
    }
}
