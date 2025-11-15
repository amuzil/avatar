package com.amuzil.carryon.physics.network.impl;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.network.packets.api.AvatarPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ForceCloudCollisionPacket implements AvatarPacket {

    public static final CustomPacketPayload.Type<ForceCloudCollisionPacket> TYPE =
            new CustomPacketPayload.Type<>(Avatar.id(ForceCloudCollisionPacket.class));

    public static final StreamCodec<FriendlyByteBuf, ForceCloudCollisionPacket> CODEC =
            StreamCodec.ofMember(ForceCloudCollisionPacket::toBytes, ForceCloudCollisionPacket::new);

    private final String cloudAId;
    private final String cloudBId;

    private final Vec3 contactPos;
    private final float radius;

    private final float densityA;
    private final float densityB;

    private final Vec3 avgVelA;
    private final Vec3 avgVelB;

    private final float intensity;

    // SERVER-SIDE ctor
    public ForceCloudCollisionPacket(
            String cloudAId,
            String cloudBId,
            Vec3 contactPos,
            float radius,
            float densityA,
            float densityB,
            Vec3 avgVelA,
            Vec3 avgVelB,
            float intensity
    ) {
        this.cloudAId = cloudAId;
        this.cloudBId = cloudBId;
        this.contactPos = contactPos;
        this.radius = radius;
        this.densityA = densityA;
        this.densityB = densityB;
        this.avgVelA = avgVelA;
        this.avgVelB = avgVelB;
        this.intensity = intensity;
    }

    // DECODER ctor
    public ForceCloudCollisionPacket(FriendlyByteBuf buf) {
        this.cloudAId = buf.readUtf();
        this.cloudBId = buf.readUtf();

        this.contactPos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.radius = buf.readFloat();

        this.densityA = buf.readFloat();
        this.densityB = buf.readFloat();

        this.avgVelA = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.avgVelB = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());

        this.intensity = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.cloudAId);
        buf.writeUtf(this.cloudBId);

        buf.writeDouble(this.contactPos.x());
        buf.writeDouble(this.contactPos.y());
        buf.writeDouble(this.contactPos.z());
        buf.writeFloat(this.radius);

        buf.writeFloat(this.densityA);
        buf.writeFloat(this.densityB);

        buf.writeDouble(this.avgVelA.x());
        buf.writeDouble(this.avgVelA.y());
        buf.writeDouble(this.avgVelA.z());

        buf.writeDouble(this.avgVelB.x());
        buf.writeDouble(this.avgVelB.y());
        buf.writeDouble(this.avgVelB.z());

        buf.writeFloat(this.intensity);
    }

    public static void handle(ForceCloudCollisionPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!ctx.flow().getReceptionSide().isClient()) return;

            ClientForceSystem system = ClientForceSystem.get();
            ClientForceCloud a = system.getCloud(msg.cloudAId);
            ClientForceCloud b = system.getCloud(msg.cloudBId);
            if (a == null || b == null) return;

            // Delegate to visual logic
            a.applyCollisionEffect(
                    msg.contactPos,
                    msg.radius,
                    msg.densityA,
                    msg.avgVelA,
                    msg.densityB,
                    msg.avgVelB,
                    msg.intensity,
                    true
            );
            b.applyCollisionEffect(
                    msg.contactPos,
                    msg.radius,
                    msg.densityB,
                    msg.avgVelB,
                    msg.densityA,
                    msg.avgVelA,
                    msg.intensity,
                    false
            );
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
