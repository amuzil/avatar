package com.amuzil.caliber.physics.bullet.collision.space.generator;

import com.amuzil.av3.entity.construct.AvatarRigidBlock;
import com.amuzil.caliber.physics.bullet.collision.body.ElementRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.caliber.physics.bullet.collision.space.MinecraftSpace;
import com.jme3.math.Vector3f;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Mods should implement it on their own, which will allow for better performance
 */
public class EntityCollisionGenerator {
    public static void step(MinecraftSpace space) {
        for (var rigidBody: space.getRigidBodiesByClass(EntityRigidBody.class)) {
//            if (rigidBody.getElement().skipVanillaEntityCollisions())
//                continue;

            final var box = rigidBody.getBoundingBox();
            final var rigidBodyAABB = rigidBody.getMinecraftBoundingBox();

            Entity rigidBodyEntity = rigidBody.getElement().cast();
            if (!(rigidBodyEntity instanceof AvatarRigidBlock rigidBlock)) continue;
            rigidBlock.syncFromPhysics();
            Vec3 current = rigidBlock.position();
            Vec3 lastPos = new Vec3(rigidBlock.xOld, rigidBlock.yOld, rigidBlock.zOld);
            Vec3 delta = current.subtract(lastPos);

            for (var entity: space.getWorkerThread().getEntitySupplier().getInsideOf(rigidBody, rigidBodyAABB)) {
                if (rigidBody.getPriorityPlayer() != null && entity.getId() == rigidBody.getPriorityPlayer().getId()) continue;
                AABB entityAABB = entity.getBoundingBox();

                if (entityAABB.intersects(rigidBodyAABB)) {
                    // Move ENTITY out of physics block
                    Vector3f mtv = computeMTV(entityAABB, rigidBodyAABB);
                    Vec3 newPos = entity.position().add(mtv.x, mtv.y, mtv.z);
                    entity.setPos(newPos);

                    // NOW cancel velocity in MTV axis to prevent sliding
                    Vec3 vel = entity.getDeltaMovement();

                    if (Math.abs(mtv.x) > 0.0001)
                        vel = new Vec3(0, vel.y, vel.z);

                    boolean pushedUp = false;
                    if (Math.abs(mtv.y) > 0.0001) {
                        vel = new Vec3(vel.x, 0, vel.z);
                        entity.setOnGround(true);
                        pushedUp = true; // standing on rigid body
                    }

                    if (Math.abs(mtv.z) > 0.0001)
                        vel = new Vec3(vel.x, vel.y, 0);

                    entity.setDeltaMovement(vel);

                    if (pushedUp && delta.lengthSqr() > 1e-9) {
                        // Separate horizontal and vertical components
                        Vec3 horizontalDelta = new Vec3(delta.x, 0, delta.z).scale(0.5); // scale horizontal
                        Vec3 verticalDelta = new Vec3(0, delta.y, 0); // full vertical

                        // Move horizontally first
                        entity.move(MoverType.PISTON, horizontalDelta);

                        // Then move vertically to stay on top
                        entity.move(MoverType.PISTON, verticalDelta);

                        // Blend velocity for smooth walking
                        entity.setDeltaMovement(entity.getDeltaMovement().add(horizontalDelta).add(verticalDelta));

                        // Keep grounded
                        entity.setOnGround(true);
                    }
                }
            }
        }
    }

    private static Vector3f computeMTV(AABB a, AABB b) {
        double dx1 = b.maxX - a.minX; // push A left
        double dx2 = a.maxX - b.minX; // push A right
        double dz1 = b.maxZ - a.minZ; // push A forward
        double dz2 = a.maxZ - b.minZ; // push A backward
        double dy1 = b.maxY - a.minY; // push A down
        double dy2 = a.maxY - b.minY; // push A up

        // MTV candidates
        double[] overlap = { dx1, -dx2, dz1, -dz2, dy1, -dy2 };

        // Find smallest absolute displacement
        double best = Double.MAX_VALUE;
        int index = -1;

        for (int i = 0; i < overlap.length; i++) {
            double v = overlap[i];
            if (Math.abs(v) < Math.abs(best)) {
                best = v;
                index = i;
            }
        }

        // Return MTV as vector
        return switch (index) {
            case 0, 1 -> new Vector3f((float) best, 0, 0);
            case 2, 3 -> new Vector3f(0, 0, (float) best);
            case 4, 5 -> new Vector3f(0, (float) best, 0);
            default -> new Vector3f();
        };
    }
}