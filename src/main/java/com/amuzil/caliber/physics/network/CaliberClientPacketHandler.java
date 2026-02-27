package com.amuzil.caliber.physics.network;

import com.amuzil.caliber.api.EntityPhysicsElement;
import com.amuzil.caliber.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.caliber.physics.bullet.collision.space.MinecraftSpace;
import com.amuzil.caliber.physics.bullet.math.Convert;
import com.amuzil.caliber.physics.network.impl.SyncMovementPacket;
import com.amuzil.caliber.physics.network.impl.SyncPropertiesPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;


public class CaliberClientPacketHandler {
    public static void handleRigidBodyMovementPacket(SyncMovementPacket packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            Entity entity = mc.level.getEntity(packet.getId());
            if (EntityPhysicsElement.is(entity)) {
                EntityRigidBody rigidBody = EntityPhysicsElement.get(entity).getRigidBody();

                MinecraftSpace.get(mc.level).getWorkerThread().execute(() -> {
//                    AABB box = new AABB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5);
//                    MinecraftShape.Compound compoundShape = MinecraftShape.compound(null);
//                    MinecraftShape.Convex shape = MinecraftShape.convex(box);
//                    compoundShape.addChildShape(shape, 0, -1, 0);
//                    compoundShape.addChildShape(shape, 0,  0, 0);
//                    compoundShape.addChildShape(shape, 0,  1, 0);
//                    rigidBody.setCollisionShape(compoundShape);
                    rigidBody.setPhysicsRotation(Convert.toBullet(packet.getRotation()));
                    rigidBody.setPhysicsLocation(Convert.toBullet(packet.getPos()));
                    rigidBody.setLinearVelocity(Convert.toBullet(packet.getLinearVel()));
                    rigidBody.setAngularVelocity(Convert.toBullet(packet.getAngularVel()));
                    rigidBody.activate();
                });
            }
        }
    }

    public static void handleRigidBodyPropertiesPacket(SyncPropertiesPacket packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            Entity entity = mc.level.getEntity(packet.getId());
            if (EntityPhysicsElement.is(entity)) {
                EntityRigidBody rigidBody = EntityPhysicsElement.get(entity).getRigidBody();

                MinecraftSpace.get(mc.level).getWorkerThread().execute(() -> {
                    rigidBody.setMass(packet.getMass());
                    rigidBody.setDragCoefficient(packet.getDragCoefficient());
                    rigidBody.setFriction(packet.getFriction());
                    rigidBody.setRestitution(packet.getRestitution());
                    rigidBody.setTerrainLoadingEnabled(packet.isTerrainLoadingEnabled());
                    rigidBody.setBuoyancyType(packet.getBuoyancyType());
                    rigidBody.setDragType(packet.getDragType());
                    if (packet.getPriorityPlayer() != null)
                        rigidBody.prioritize(mc.level.getPlayerByUUID(packet.getPriorityPlayer()));
                    else
                        rigidBody.prioritize(null);
                    rigidBody.activate();
                });
            }
        }
    }

//    public static void handleSyncCollisionShapePacket(SyncCollisionShapePacket packet) {
//        // TODO: Fix so that it can set any generic CollisionShape
//        Minecraft mc = Minecraft.getInstance();
//        if (mc.level != null) {
//            Entity entity = mc.level.getEntity(packet.getId());
//            if (EntityPhysicsElement.is(entity)) {
//                EntityRigidBody rigidBody = EntityPhysicsElement.get(entity).getRigidBody();
//                MinecraftSpace.get(mc.level).getWorkerThread().execute(() -> {
//                });
//            }
//        }
//    }
}
