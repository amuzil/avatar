package com.amuzil.carryon.physics.network.impl;


import com.amuzil.av3.Avatar;
import com.amuzil.av3.network.packets.api.AvatarPacket;
import com.amuzil.carryon.physics.network.CarryonPacket;
import com.amuzil.magus.physics.core.ForceCloud;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
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
    private final ResourceLocation type;
    private final long seed;

    private final int gridNx, gridNy, gridNz;
    private final float cellSize;

    private final Vec3 origin;
    private final Vec3 direction;
    private final float length;
    private final float radius;

    // SERVER-SIDE CONSTRUCTOR
    public ForceCloudSpawnPacket(ForceCloud cloud) {
        super(true); // clientbound
        this.id = cloud.id();
        this.ownerUuid = cloud.getOwnerUuid();
        this.type = cloud.getType();          // e.g. ResourceLocation
        this.seed = cloud.getSeed();          // or 0L if you don't use it yet

        this.gridNx = cloud.gridNx();
        this.gridNy = cloud.gridNy();
        this.gridNz = cloud.gridNz();
        this.cellSize = cloud.cellSize();

        this.origin = cloud.getOrigin();      // or cloud.pos()
        this.direction = cloud.vel();// or cloud.dir()
        this.length = cloud.getLength();
        this.radius = cloud.getRadius();
    }

    // CLIENT-SIDE DECODING CONSTRUCTOR
    public ForceCloudSpawnPacket(FriendlyByteBuf buf) {
        this.id = buf.readUtf();
        this.ownerUuid = buf.readUUID();
        this.type = buf.readResourceLocation();
        this.seed = buf.readLong();

        this.gridNx = buf.readVarInt();
        this.gridNy = buf.readVarInt();
        this.gridNz = buf.readVarInt();
        this.cellSize = buf.readFloat();

        this.origin = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.direction = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.length = buf.readFloat();
        this.radius = buf.readFloat();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.id);
        buf.writeUUID(this.ownerUuid);
        buf.writeResourceLocation(this.type);
        buf.writeLong(this.seed);

        buf.writeVarInt(this.gridNx);
        buf.writeVarInt(this.gridNy);
        buf.writeVarInt(this.gridNz);
        buf.writeFloat(this.cellSize);

        buf.writeDouble(this.origin.x);
        buf.writeDouble(this.origin.y);
        buf.writeDouble(this.origin.z);

        buf.writeDouble(this.direction.x);
        buf.writeDouble(this.direction.y);
        buf.writeDouble(this.direction.z);

        buf.writeFloat(this.length);
        buf.writeFloat(this.radius);
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

    public ResourceLocation getTypeId() {
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

    public float getLength() {
        return length;
    }

    public float getRadius() {
        return radius;
    }

    public static void handle(ForceCloudCollisionPacket msg, IPayloadContext ctx) {
    }
}