package com.amuzil.caliber.physics.network.impl;

import com.amuzil.caliber.CaliberPhysics;
import com.amuzil.caliber.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.shape.MinecraftShape;
import com.amuzil.caliber.physics.bullet.math.Convert;
import com.amuzil.caliber.physics.network.CaliberClientPacketHandler;
import com.amuzil.caliber.physics.network.CaliberPacket;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.infos.ChildCollisionShape;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public class SyncCollisionShapePacket extends CaliberPacket {
    public static final Type<SyncCollisionShapePacket> TYPE = new Type<>(CaliberPhysics.id(SyncCollisionShapePacket.class));
    public static final StreamCodec<FriendlyByteBuf, SyncCollisionShapePacket> STREAM_CODEC =
            StreamCodec.ofMember(SyncCollisionShapePacket::toBytes, SyncCollisionShapePacket::new);

    private final int id;
    private final List<AABB> boxes;

    public SyncCollisionShapePacket(EntityRigidBody body) {
        super(true);
        this.id = body.getElement().cast().getId();
        this.boxes = new ArrayList<>();
        if (body.getCollisionShape() instanceof CompoundCollisionShape compoundCollisionShape) {
            for (ChildCollisionShape childShape: compoundCollisionShape.listChildren()) {
                if (childShape.getShape() instanceof MinecraftShape.Convex convex) {
                    BoundingBox boundingBox = convex.boundingBox(new Vector3f(), new Quaternion(), new BoundingBox());
                    AABB aabb = Convert.toMinecraft(boundingBox);
                    boxes.add(aabb);
                }
            }
        }
    }

    public SyncCollisionShapePacket(FriendlyByteBuf buf) {
        super(true);
        this.id = buf.readVarInt();

        // Read AABB list
        int count = buf.readVarInt();
        this.boxes = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            double minX = buf.readDouble();
            double minY = buf.readDouble();
            double minZ = buf.readDouble();
            double maxX = buf.readDouble();
            double maxY = buf.readDouble();
            double maxZ = buf.readDouble();
            boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ));
        }
    }

    @Override
    protected void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(this.id);

        // Write AABB list
        buf.writeVarInt(this.boxes.size());
        for (AABB box: this.boxes) {
            buf.writeDouble(box.minX);
            buf.writeDouble(box.minY);
            buf.writeDouble(box.minZ);
            buf.writeDouble(box.maxX);
            buf.writeDouble(box.maxY);
            buf.writeDouble(box.maxZ);
        }
    }

    public int getId() {
        return this.id;
    }

    public List<AABB> getBoxes() {
        return this.boxes;
    }

    @Override
    public Runnable getProcessor(IPayloadContext context) {
        return client(context, () -> CaliberClientPacketHandler.handleSyncCollisionShapePacket(this));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

//    public enum ShapeType {
//        BOX,
//        CONVEX,
//        CONCAVE,
//        COMPOUND
//    }
}
