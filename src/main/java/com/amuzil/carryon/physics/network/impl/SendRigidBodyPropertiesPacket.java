package com.amuzil.carryon.physics.network.impl;

import com.amuzil.carryon.CarryOn;
import com.amuzil.carryon.physics.bullet.collision.body.ElementRigidBody;
import com.amuzil.carryon.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.carryon.physics.network.CarryonPacket;
import com.amuzil.carryon.physics.network.CarryonClientPacketHandler;
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

    private int id;
    private float mass;
    private float dragCoefficient;
    private float friction;
    private float restitution;
    private boolean terrainLoadingEnabled;
    private ElementRigidBody.BuoyancyType buoyancyType;
    private ElementRigidBody.DragType dragType;
    private @Nullable UUID priorityPlayer;

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
    protected void fromBytes(FriendlyByteBuf buffer) {
        this.id = buffer.readVarInt();
        this.mass = buffer.readFloat();
        this.dragCoefficient = buffer.readFloat();
        this.friction = buffer.readFloat();
        this.restitution = buffer.readFloat();
        this.terrainLoadingEnabled = buffer.readBoolean();
        this.buoyancyType = buffer.readEnum(ElementRigidBody.BuoyancyType.class);
        this.dragType = buffer.readEnum(ElementRigidBody.DragType.class);
        this.priorityPlayer = buffer.readNullable(FriendlyByteBuf::readUUID);
    }

    @Override
    protected void toBytes(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.id);
        buffer.writeFloat(this.mass);
        buffer.writeFloat(this.dragCoefficient);
        buffer.writeFloat(this.friction);
        buffer.writeFloat(this.restitution);
        buffer.writeBoolean(this.terrainLoadingEnabled);
        buffer.writeEnum(this.buoyancyType);
        buffer.writeEnum(this.dragType);
        buffer.writeNullable(this.priorityPlayer, FriendlyByteBuf::writeUUID);
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
