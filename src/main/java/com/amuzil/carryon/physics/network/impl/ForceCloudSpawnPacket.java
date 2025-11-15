package com.amuzil.carryon.physics.network.impl;


import com.amuzil.av3.Avatar;
import com.amuzil.av3.network.packets.api.AvatarPacket;
import com.amuzil.av3.utils.network.AvatarPacketUtils;
import com.amuzil.magus.physics.core.ForceCloud;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
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

    private final int gridNx, gridNy, gridNz;
    private final float cellSize;

    private final Vec3 origin;
    private final Vec3 direction;
    private final Vec3 aabbMin;
    private final Vec3 aabbMax;

    // SERVER-SIDE CONSTRUCTOR
    public ForceCloudSpawnPacket(ForceCloud cloud) {
        this.id = cloud.id();
        this.ownerUuid = cloud.owner();
        this.type = cloud.type();          // e.g. ResourceLocation
        this.seed = cloud.seed();          // or 0L if you don't use it yet

        this.gridNx = cloud.grid().binX();
        this.gridNy = cloud.grid().binY();
        this.gridNz = cloud.grid().binZ();
        this.cellSize = cloud.grid().cellSize();

        this.origin = cloud.grid().origin();      // or cloud.pos()
        this.direction = cloud.vel();// or cloud.dir()
        this.aabbMin = cloud.bounds().getMinPosition();
        this.aabbMax = cloud.bounds().getMaxPosition();
    }

    // CLIENT-SIDE DECODING CONSTRUCTOR
    public ForceCloudSpawnPacket(FriendlyByteBuf buf) {
        this.id = buf.readUtf();
        this.ownerUuid = buf.readUUID();
        this.type = buf.readInt();
        this.seed = buf.readLong();

        this.gridNx = buf.readVarInt();
        this.gridNy = buf.readVarInt();
        this.gridNz = buf.readVarInt();
        this.cellSize = buf.readFloat();

        this.origin = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.direction = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.aabbMin = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.aabbMax = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static void handle(ForceCloudSpawnPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (net.minecraft.client.Minecraft.getInstance().level == null)
                return;
            // Mirror only: build/update the client cloud from packet contents
            ClientForceSystem.get().spawnFromPacket(msg);
        });
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.id);
        buf.writeUUID(this.ownerUuid);
        buf.writeInt(this.type);
        buf.writeLong(this.seed);

        buf.writeVarInt(this.gridNx);
        buf.writeVarInt(this.gridNy);
        buf.writeVarInt(this.gridNz);
        buf.writeFloat(this.cellSize);

        AvatarPacketUtils.writeVec3(origin, buf);

        AvatarPacketUtils.writeVec3(direction, buf);

        AvatarPacketUtils.writeVec3(aabbMin, buf);

        AvatarPacketUtils.writeVec3(aabbMax, buf);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // GETTERS
    public String getId() {
        return id;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public int typeID() {
        return type;
    }

    public long getSeed() {
        return seed;
    }

    public int getGridNx() {
        return gridNx;
    }

    public int getGridNy() {
        return gridNy;
    }

    public int getGridNz() {
        return gridNz;
    }

    public float getCellSize() {
        return cellSize;
    }

    public Vec3 getOrigin() {
        return origin;
    }

    public Vec3 getDirection() {
        return direction;
    }

    public Vec3 aabbMin() {
        return this.aabbMin;
    }

    public Vec3 aabbMax() {
        return this.aabbMax;
    }
}