package com.amuzil.caliber.physics.network.impl;

import com.amuzil.caliber.CaliberPhysics;
import com.amuzil.caliber.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.caliber.physics.bullet.math.Convert;
import com.amuzil.caliber.physics.network.CaliberClientPacketHandler;
import com.amuzil.caliber.physics.network.CaliberPacket;
import com.jme3.math.Quaternion;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SyncMovementPacket extends CaliberPacket {
    public static final Type<SyncMovementPacket> TYPE = new Type<>(CaliberPhysics.id(SyncMovementPacket.class));
    public static final StreamCodec<FriendlyByteBuf, SyncMovementPacket> STREAM_CODEC =
            StreamCodec.ofMember(SyncMovementPacket::toBytes, SyncMovementPacket::new);

    private final int id;
    private final Quaternionf rotation;
    private final Vector3f pos;
    private final Vector3f linearVel;
    private final Vector3f angularVel;

    public SyncMovementPacket(EntityRigidBody body) {
        super(true);
        this.id = body.getElement().cast().getId();
        this.rotation = Convert.toMinecraft(body.getPhysicsRotation(new Quaternion()));
        this.pos = Convert.toMinecraft(body.getPhysicsLocation(new com.jme3.math.Vector3f()));
        this.linearVel = Convert.toMinecraft(body.getLinearVelocity(new com.jme3.math.Vector3f()));
        this.angularVel = Convert.toMinecraft(body.getAngularVelocity(new com.jme3.math.Vector3f()));
    }

    public SyncMovementPacket(FriendlyByteBuf buf) {
        super(true);
        this.id = buf.readVarInt();
        this.rotation = buf.readQuaternion();
        this.pos = buf.readVector3f();
        this.linearVel = buf.readVector3f();
        this.angularVel = buf.readVector3f();
    }

    public int getId() {
        return this.id;
    }

    public Quaternionf getRotation() {
        return this.rotation;
    }

    public Vector3f getPos() {
        return this.pos;
    }

    public Vector3f getLinearVel() {
        return this.linearVel;
    }

    public Vector3f getAngularVel() {
        return this.angularVel;
    }

    @Override
    protected void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeQuaternion(this.rotation);
        buf.writeVector3f(this.pos);
        buf.writeVector3f(this.linearVel);
        buf.writeVector3f(this.angularVel);
    }

    @Override
    public Runnable getProcessor(IPayloadContext context) {
        return client(context, () -> CaliberClientPacketHandler.handleRigidBodyMovementPacket(this));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
