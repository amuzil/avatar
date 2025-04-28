package com.amuzil.omegasource.network.packets.client;

import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.bending.BendingForm;
import com.amuzil.omegasource.entity.ElementProjectile;
import com.amuzil.omegasource.events.FormActivatedEvent;
import com.amuzil.omegasource.network.AvatarNetwork;
import com.amuzil.omegasource.network.packets.api.AvatarPacket;
import com.amuzil.omegasource.api.magus.registry.Registries;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.amuzil.omegasource.Avatar.MOD_ID;
import static com.amuzil.omegasource.bending.BendingForms.*;


public class FormActivatedPacket implements AvatarPacket {

    private final BendingForm form;
    private final Element element;
    private final int entityId; // Entity ID to send back to client for FX

    public FormActivatedPacket(BendingForm form, Element element, int entityId) {
        this.form = Objects.requireNonNullElseGet(form, BendingForm::new);
        this.element = element;
        this.entityId = entityId;
    }

    public void toBytes(FriendlyByteBuf buf) {
        if (form != null) {
            buf.writeUtf(form.name());
            buf.writeUtf(element.name());
            buf.writeInt(entityId);
        }
    }

    public static FormActivatedPacket fromBytes(FriendlyByteBuf buf) {
        String formName = buf.readUtf();
        String elementName = buf.readUtf();
        int entityId = buf.readInt();
        BendingForm form = (BendingForm) Registries.FORMS.get().getValue(ResourceLocation.fromNamespaceAndPath(MOD_ID, formName));
        Element element = (Element) Registries.SKILL_CATEGORIES.get().getValue(ResourceLocation.fromNamespaceAndPath(MOD_ID, elementName));
        return new FormActivatedPacket(form, element, entityId);
    }

    // Server-side handler
    public static void handleServerSide(BendingForm form, Element element, int entityId, ServerPlayer player) {
        // Perform server-side entity spawning and updating logic and fire Form Event here
//        MinecraftForge.EVENT_BUS.post(new FormActivatedEvent(form, player));
        ServerLevel level = player.serverLevel();
        // TODO - Create/perform certain entity updates based on form and element
        //      - All Skills/techniques should be determined and handled here
        ElementProjectile entity;
        if (entityId != 0) { // Update existing entity
            entity = (ElementProjectile) player.level().getEntity(entityId);
        } else { // Create new entity
            entity = ElementProjectile.createElementEntity(form, element, player, level);
        }
        assert entity != null;
        if (form.equals(ARC) || form.equals(NULL)) {
            entity.control(1.5f, form);
        } else if (form.equals(STRIKE) || form.equals(BLOCK)) {
            entity.shoot(player.getViewVector(1).x, player.getViewVector(1).y, player.getViewVector(1).z, 1, 1);
        } else {
            entity.discard();
            return; // Unhandled Form - Discard and print no effects
        }
        if (entityId == 0)
            level.addFreshEntity(entity);
        FormActivatedPacket packet = new FormActivatedPacket(form, element, entityId);
        Predicate<ServerPlayer> predicate = (serverPlayer) -> player.distanceToSqr(serverPlayer) < 2500;
        for (ServerPlayer nearbyPlayer: level.getPlayers(predicate.and(LivingEntity::isAlive))) {
            AvatarNetwork.sendToClient(packet, nearbyPlayer);
        }

        // Keep this in case we want a more specific client packet distribution filter
        /** Distribute packet to clients within 500 blocks **/
//        AvatarNetwork.CHANNEL.send(PacketDistributor.NEAR.with(
//                () -> new PacketDistributor.TargetPoint(player.getX(), player.getY(), player.getZ(),
//                        500, level.dimension())), packet);
    }

    // Client-side handler
    @OnlyIn(Dist.CLIENT)
    private static void handleClientSide(BendingForm form, int entityId) {
        // Perform client-side particle effect or other rendering logic here
        Player player = Minecraft.getInstance().player;
        assert player != null;
        ElementProjectile elementProjectile = (ElementProjectile) player.level().getEntity(entityId);
        /**
         NOTE: Need to ensure ElementProjectile's extra constructor args are set client-side.
         @see ElementProjectile#ElementProjectile(EntityType, Level) This gets called first and server-side only.
         Can't change this default constructor because it's needed to register entities. Add/use any extra args to Packet.
         */
        assert elementProjectile != null;
        elementProjectile.startEffect(form, player);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClientSide(form, entityId));
            } else {
                ServerPlayer player = ctx.get().getSender();
                assert player != null;
                handleServerSide(form, element, entityId, player);
            }
        });
        return true;
    }
}
