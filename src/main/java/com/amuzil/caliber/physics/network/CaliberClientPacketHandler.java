package com.amuzil.caliber.physics.network;

import com.amuzil.caliber.api.EntityPhysicsElement;
import com.amuzil.caliber.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.shape.MinecraftShape;
import com.amuzil.caliber.physics.bullet.collision.space.MinecraftSpace;
import com.amuzil.caliber.physics.bullet.math.Convert;
import com.amuzil.caliber.physics.network.impl.SyncCollisionShapePacket;
import com.amuzil.caliber.physics.network.impl.SendRigidBodyMovementPacket;
import com.amuzil.caliber.physics.network.impl.SendRigidBodyPropertiesPacket;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;


public class CaliberClientPacketHandler {
    public static void handleRigidBodyMovementPacket(SendRigidBodyMovementPacket packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            Entity entity = mc.level.getEntity(packet.getId());
            if (EntityPhysicsElement.is(entity)) {
                EntityRigidBody rigidBody = EntityPhysicsElement.get(entity).getRigidBody();

                MinecraftSpace.get(mc.level).getWorkerThread().execute(() -> {
                    rigidBody.setPhysicsRotation(Convert.toBullet(packet.getRotation()));
                    rigidBody.setPhysicsLocation(Convert.toBullet(packet.getPos()));
                    rigidBody.setLinearVelocity(Convert.toBullet(packet.getLinearVel()));
                    rigidBody.setAngularVelocity(Convert.toBullet(packet.getAngularVel()));
                    rigidBody.activate();
                });
            }
        }
    }

    public static void handleRigidBodyPropertiesPacket(SendRigidBodyPropertiesPacket packet) {
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

    public static void handleSyncCollisionShapePacket(SyncCollisionShapePacket packet) {
        // TODO: Fix so that it can set any genric CollisionShape
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            Entity entity = mc.level.getEntity(packet.getId());
            if (EntityPhysicsElement.is(entity)) {
                EntityRigidBody rigidBody = EntityPhysicsElement.get(entity).getRigidBody();
//                MinecraftSpace.get(mc.level).getWorkerThread().execute(() -> {
//                    List<MinecraftShape> shapes = new ArrayList<>();
//                    packet.getBoxes().forEach(box -> shapes.add(MinecraftShape.convex(box)));
//                    rigidBody.setCollisionShape(MinecraftShape.compound(shapes));
//                    rigidBody.activate();
//                });
            }
        }
    }
}
