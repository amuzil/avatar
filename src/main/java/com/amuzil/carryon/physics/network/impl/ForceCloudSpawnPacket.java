package com.amuzil.carryon.physics.network.impl;


import com.amuzil.av3.Avatar;
import com.amuzil.av3.network.packets.api.AvatarPacket;
import com.amuzil.av3.utils.network.AvatarPacketUtils;
import com.amuzil.carryon.physics.bullet.collision.space.MinecraftSpace;
import com.amuzil.magus.physics.core.ForceCloud;
import com.amuzil.magus.physics.core.ForceSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public class ForceCloudSpawnPacket implements AvatarPacket {

    public static final Type<ForceCloudSpawnPacket> TYPE =
            new CustomPacketPayload.Type<>(Avatar.id(ForceCloudSpawnPacket.class));

    public static final StreamCodec<FriendlyByteBuf, ForceCloudSpawnPacket> CODEC =
            StreamCodec.ofMember(ForceCloudSpawnPacket::toBytes, ForceCloudSpawnPacket::new);

    private final String id;
    private final UUID ownerUuid;
    private final int type;
    private final long seed;
    private final int maxPoints;

    private final int gridNx, gridNy, gridNz;
    private final float cellSize;

    private final Vec3 origin;
    private final Vec3 direction;
    private final Vec3 force;
    private final Vec3 aabbMin;
    private final Vec3 aabbMax;

    private final double lifetime;

    // SERVER-SIDE CONSTRUCTOR
    public ForceCloudSpawnPacket(ForceCloud cloud) {
        this.id = cloud.id();
        this.ownerUuid = cloud.owner();
        this.type = cloud.type();          // e.g. ResourceLocation
        this.seed = cloud.seed();          // or 0L if you don't use it yet
        this.maxPoints = cloud.grid().maxPoints();

        this.gridNx = cloud.grid().binX();
        this.gridNy = cloud.grid().binY();
        this.gridNz = cloud.grid().binZ();
        this.cellSize = cloud.grid().cellSize();

        this.origin = cloud.pos();      // or cloud.pos()
        this.direction = cloud.vel(); // or cloud.dir()
        this.force = cloud.force();
        this.aabbMin = cloud.bounds().getMinPosition();
        this.aabbMax = cloud.bounds().getMaxPosition();

        this.lifetime = cloud.lifetime();
    }

    // CLIENT-SIDE DECODING CONSTRUCTOR
    public ForceCloudSpawnPacket(FriendlyByteBuf buf) {
        this.id = buf.readUtf();
        this.ownerUuid = buf.readUUID();
        this.type = buf.readInt();
        this.seed = buf.readLong();
        this.maxPoints = buf.readInt();

        this.gridNx = buf.readVarInt();
        this.gridNy = buf.readVarInt();
        this.gridNz = buf.readVarInt();
        this.cellSize = buf.readFloat();

        this.origin = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.direction = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.force = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.aabbMin = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.aabbMax = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());

        this.lifetime = buf.readDouble();
    }

    public static void handle(ForceCloudSpawnPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();

            if (mc.level == null)
                return;
            // Mirror only: build/update the client cloud from packet contents

            Level level = mc.level;
            MinecraftSpace space = MinecraftSpace.get(level);
            if (!space.isServer()) {
                System.out.println("Isn't server.");
                space.getWorkerThread().execute(() -> space.addForceCloud(msg.cloud()));
            }
        });
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.id);
        buf.writeUUID(this.ownerUuid);
        buf.writeInt(this.type);
        buf.writeLong(this.seed);
        buf.writeInt(this.maxPoints);

        buf.writeVarInt(this.gridNx);
        buf.writeVarInt(this.gridNy);
        buf.writeVarInt(this.gridNz);
        buf.writeFloat(this.cellSize);

        AvatarPacketUtils.writeVec3(origin, buf);

        AvatarPacketUtils.writeVec3(direction, buf);

        AvatarPacketUtils.writeVec3(force, buf);

        AvatarPacketUtils.writeVec3(aabbMin, buf);

        AvatarPacketUtils.writeVec3(aabbMax, buf);

        buf.writeDouble(this.lifetime);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public ForceCloud cloud() {
        // Client doesn't need multithreading and we don't need it for now anyway
        ForceCloud cloud = new ForceCloud(type, maxPoints, id, origin, direction, force, ownerUuid, null);
        cloud.setLifetimeSeconds(lifetime);
        return cloud;
    }
}