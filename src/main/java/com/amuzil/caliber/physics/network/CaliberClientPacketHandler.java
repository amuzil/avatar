package com.amuzil.caliber.physics.network;

import com.amuzil.caliber.api.EntityPhysicsElement;
import com.amuzil.caliber.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.ForceRigidBody;
import com.amuzil.caliber.physics.bullet.collision.space.MinecraftSpace;
import com.amuzil.caliber.physics.bullet.math.Convert;
import com.amuzil.caliber.physics.network.impl.SendForceRigidBodyMovementPacket;
import com.amuzil.caliber.physics.network.impl.SendForceRigidBodyPropertiesPacket;
import com.amuzil.caliber.physics.network.impl.SendRigidBodyMovementPacket;
import com.amuzil.caliber.physics.network.impl.SendRigidBodyPropertiesPacket;
import com.amuzil.magus.physics.core.ForceCloud;
import com.amuzil.magus.physics.core.ForceElement;
import com.amuzil.magus.physics.core.ForcePhysicsElement;
import com.amuzil.magus.physics.core.ForcePoint;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;


public class CaliberClientPacketHandler {
    public static void handleSendRigidBodyMovementPacket(SendRigidBodyMovementPacket packet) {
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

    public static void handleSendRigidBodyPropertiesPacket(SendRigidBodyPropertiesPacket packet) {
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


    public static void handleSendRigidBodyMovementPacket(SendForceRigidBodyMovementPacket packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            MinecraftSpace space = MinecraftSpace.get(mc.level);
            if (!space.isEmpty()) {
                ForceElement element = null;
                for (ForceCloud cloud : space.getForceSystem().clouds()) {
                    if (cloud.id().equals(packet.id())) {
                        element = cloud;
                        break;
                    }
                    for (ForcePoint point : cloud.points()) {
                        if (point.id().equals(packet.id())) {
                            element = point;
                            break;
                        }
                    }
                    if (element != null) break;

                }

                // Makes sure a valid element was found
                if (element == null)
                    return;

                ForceRigidBody rigidBody = element.getRigidBody();

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

    public static void handleSendRigidBodyPropertiesPacket(SendForceRigidBodyPropertiesPacket packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            MinecraftSpace space = MinecraftSpace.get(mc.level);
            if (!space.isEmpty()) {
                ForceElement element = null;
                for (ForceCloud cloud : space.getForceSystem().clouds()) {
                    if (cloud.id().equals(packet.id())) {
                        element = cloud;
                        break;
                    }
                    for (ForcePoint point : cloud.points()) {
                        if (point.id().equals(packet.id())) {
                            element = point;
                            break;
                        }
                    }
                    if (element != null) break;

                }

                // Makes sure a valid element was found
                if (element == null)
                    return;

                ForceRigidBody rigidBody = element.getRigidBody();

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
}
