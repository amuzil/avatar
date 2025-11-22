package com.amuzil.caliber.physics.network.impl;

import com.amuzil.caliber.CaliberPhysics;
import com.amuzil.caliber.physics.bullet.collision.body.ElementRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.ForceRigidBody;
import com.amuzil.caliber.physics.network.CaliberClientPacketHandler;
import com.amuzil.caliber.physics.network.CaliberPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nullable;
import java.nio.charset.Charset;
import java.util.UUID;

public class SendForceRigidBodyPropertiesPacket extends CaliberPacket {
    public static final Type<SendForceRigidBodyPropertiesPacket> TYPE = new Type<>(CaliberPhysics.id(SendForceRigidBodyPropertiesPacket.class));
    public static final StreamCodec<FriendlyByteBuf, SendForceRigidBodyPropertiesPacket> CODEC =
            StreamCodec.ofMember(SendForceRigidBodyPropertiesPacket::toBytes, SendForceRigidBodyPropertiesPacket::new);

    private final String id;
    private final float mass;
    private final float dragCoefficient;
    private final float friction;
    private final float restitution;
    private final boolean terrainLoadingEnabled;
    private final ElementRigidBody.BuoyancyType buoyancyType;
    private final ElementRigidBody.DragType dragType;
    private final @Nullable UUID priorityPlayer;

    public SendForceRigidBodyPropertiesPacket(ForceRigidBody body) {
        super(true);
        this.id = body.getElement().cast().id();
        this.mass = body.getMass();
        this.dragCoefficient = body.getDragCoefficient();
        this.friction = body.getFriction();
        this.restitution = body.getRestitution();
        this.terrainLoadingEnabled = body.terrainLoadingEnabled();
        this.buoyancyType = body.getBuoyancyType();
        this.dragType = body.getDragType();
        this.priorityPlayer = body.getPriorityPlayer() != null ? body.getPriorityPlayer().getUUID() : null;
    }

    public SendForceRigidBodyPropertiesPacket(FriendlyByteBuf buf) {
        super(true);
        this.id = buf.readUtf();
        this.mass = buf.readFloat();
        this.dragCoefficient = buf.readFloat();
        this.friction = buf.readFloat();
        this.restitution = buf.readFloat();
        this.terrainLoadingEnabled = buf.readBoolean();
        this.buoyancyType = buf.readEnum(ElementRigidBody.BuoyancyType.class);
        this.dragType = buf.readEnum(ElementRigidBody.DragType.class);
        this.priorityPlayer = buf.readNullable(b -> b.readUUID());
    }

    public String id() {
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
        buf.writeUtf(this.id);
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
        return client(context, () -> CaliberClientPacketHandler.handleSendRigidBodyPropertiesPacket(this));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
