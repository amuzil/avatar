package com.amuzil.carryon.physics.network.impl;

import com.amuzil.carryon.CarryOn;
import com.amuzil.carryon.physics.bullet.collision.body.ElementRigidBody;
import com.amuzil.carryon.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.carryon.physics.network.CarryonClientPacketHandler;
import com.amuzil.carryon.physics.network.CarryonPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nullable;
import java.util.UUID;

public class SendRigidBodyPropertiesPacket extends CarryonPacket {
    public static final Type<SendRigidBodyPropertiesPacket> TYPE = new Type<>(CarryOn.id(SendRigidBodyPropertiesPacket.class));
    public static final StreamCodec<FriendlyByteBuf, SendRigidBodyPropertiesPacket> CODEC =
            StreamCodec.ofMember(SendRigidBodyPropertiesPacket::toBytes, SendRigidBodyPropertiesPacket::new);

    private final int id;
    private final float mass;
    private final float dragCoefficient;
    private final float friction;
    private final float restitution;
    private final boolean terrainLoadingEnabled;
    private final ElementRigidBody.BuoyancyType buoyancyType;
    private final ElementRigidBody.DragType dragType;
    private final @Nullable UUID priorityPlayer;

    public SendRigidBodyPropertiesPacket(EntityRigidBody body) {
        super(true);
        this.id = body.getElement().cast().getId();
        this.mass = body.getMass();
        this.dragCoefficient = body.getDragCoefficient();
        this.friction = body.getFriction();
        this.restitution = body.getRestitution();
        this.terrainLoadingEnabled = body.terrainLoadingEnabled();
        this.buoyancyType = body.getBuoyancyType();
        this.dragType = body.getDragType();
        this.priorityPlayer = body.getPriorityPlayer() != null ? body.getPriorityPlayer().getUUID() : null;
    }

    public SendRigidBodyPropertiesPacket(FriendlyByteBuf buf) {
        super(false);
        this.id = buf.readVarInt();
        this.mass = buf.readFloat();
        this.dragCoefficient = buf.readFloat();
        this.friction = buf.readFloat();
        this.restitution = buf.readFloat();
        this.terrainLoadingEnabled = buf.readBoolean();
        this.buoyancyType = buf.readEnum(ElementRigidBody.BuoyancyType.class);
        this.dragType = buf.readEnum(ElementRigidBody.DragType.class);
        this.priorityPlayer = buf.readNullable(b -> b.readUUID());
    }

    public int getId() {
        return this.id;
    }

    public float getMass() {
        return this.mass;
    }

    public float getDragCoefficient() {
        return this.dragCoefficient;
    }

    public float getFriction() {
        return this.friction;
    }

    public float getRestitution() {
        return this.restitution;
    }

    public boolean isTerrainLoadingEnabled() {
        return this.terrainLoadingEnabled;
    }

    public ElementRigidBody.BuoyancyType getBuoyancyType() {
        return this.buoyancyType;
    }

    public ElementRigidBody.DragType getDragType() {
        return this.dragType;
    }

    public @Nullable UUID getPriorityPlayer() {
        return this.priorityPlayer;
    }

    @Override
    protected void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeFloat(this.mass);
        buf.writeFloat(this.dragCoefficient);
        buf.writeFloat(this.friction);
        buf.writeFloat(this.restitution);
        buf.writeBoolean(this.terrainLoadingEnabled);
        buf.writeEnum(this.buoyancyType);
        buf.writeEnum(this.dragType);
        buf.writeNullable(this.priorityPlayer, (b,uuid) -> b.writeUUID(uuid));
    }

    @Override
    public Runnable getProcessor(IPayloadContext context) {
        return client(context, () -> CarryonClientPacketHandler.handleSendRigidBodyPropertiesPacket(this));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
