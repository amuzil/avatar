package com.amuzil.omegasource.utils.ship;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBdc;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;


public final class EarthController implements ShipForcesInducer {
    private final ConcurrentLinkedQueue<Vector3dc> invForces = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Vector3dc> invTorques = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Vector3dc> rotForces = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Vector3dc> rotTorques = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<InvForceAtPos> invPosForces = new ConcurrentLinkedQueue<>();

    private volatile boolean toBeStatic = false;
    private volatile boolean toBeStaticUpdated = false;
    private ServerShip ship;
    private LivingEntity entity;
    public final AtomicInteger tickCount = new AtomicInteger(0);

    @Override
    public void applyForces(@NotNull PhysShip physShip) {

        if (!invForces.isEmpty()) {
            if (physShip.isStatic())
                physShip.setStatic(false);
            Vector3dc force = invForces.poll();
            if (tickCount.get() >= 6) {
                double yForce = force.y() - 20000.0D;
                if (physShip.getVelocity().y() <= 0) {
                    yForce += 1500;
                } else {
                    yForce = 0;
                }
                Vector3d newForce = new Vector3d(0, yForce, 0);
                System.out.println("applyForces: " + physShip.getVelocity());
                physShip.applyInvariantForce(newForce);
            } else {
                physShip.applyInvariantForce(force);
            }
        }
        if (!invTorques.isEmpty())
            physShip.applyInvariantTorque(invTorques.poll());
        if (!rotForces.isEmpty())
            physShip.applyRotDependentForce(rotForces.poll());
        if (!rotTorques.isEmpty())
            physShip.applyRotDependentTorque(rotTorques.poll());
        if (!invPosForces.isEmpty()) {
            InvForceAtPos invForceAtPos = invPosForces.poll();
            physShip.applyInvariantForceToPos(
                    invForceAtPos.force(),
                    invForceAtPos.pos()
            );
        }

        if (toBeStaticUpdated) {
            physShip.setStatic(toBeStatic);
            toBeStaticUpdated = false;
        }

        checkCollision();
        tickCount.incrementAndGet();
    }

    private void checkCollision() {
        ServerLevel level = (ServerLevel) entity.level();
        Vector3dc velocity = ship.getVelocity();
        double mag = velocity.length();
        boolean isMoving = mag > 0.05;
        boolean isMovingFast = mag > 2.0;
        if (isMoving) {
            if (isMovingFast)
                checkShipShipCollisions(level, ship);
            checkShipEntityCollisions(level, ship);
        }
    }

    public void applyInvariantForce(Vector3dc force) {
        invForces.add(force);
    }

    public void applyInvariantTorque(Vector3dc torque) {
        invTorques.add(torque);
    }

    public void applyRotDependentForce(Vector3dc force) {
        rotForces.add(force);
    }

    public void applyRotDependentTorque(Vector3dc torque) {
        rotTorques.add(torque);
    }

    public void applyInvariantForceToPos(Vector3dc force, Vector3dc pos) {
        invPosForces.add(new InvForceAtPos(force, pos));
    }

    public void setStatic(boolean b) {
        toBeStatic = b;
        toBeStaticUpdated = true;
    }

    private record InvForceAtPos(Vector3dc force, Vector3dc pos) {}

    public void checkShipShipCollisions(ServerLevel level, ServerShip ship) {
        String dimensionId = VSGameUtilsKt.getDimensionId(level);
        VSGameUtilsKt.getShipObjectWorld(level).getAllShips().getIntersecting(
                ship.getWorldAABB(),
                dimensionId
        ).forEach(serverShip -> {
            if (serverShip.getId() != ship.getId()) {
                Vector3dc shipYardPos = serverShip.getTransform().getPositionInShip();
                BlockPos blockPos = BlockPos.containing(VectorConversionsMCKt.toMinecraft(shipYardPos));
                level.destroyBlock(blockPos, false);
            }
        });
    }

    public void checkShipEntityCollisions(ServerLevel level, ServerShip ship) {
        getShipEntityCollisions(level, ship.getWorldAABB()).forEach(entity -> {
            if (entity != null && entity != this.entity) {
                Vector3dc shipYardPos = ship.getTransform().getPositionInShip();
                BlockPos blockPos = BlockPos.containing(VectorConversionsMCKt.toMinecraft(shipYardPos));
                level.destroyBlock(blockPos, false);
                Vec3 motion = entity.getDeltaMovement();
                entity.addDeltaMovement(motion.scale(0.5f));
                entity.hasImpulse = true; entity.hurtMarked = true;
                entity.hurt(entity.damageSources().thrown(entity, entity), 4f);
            }
        });
    }

    public static List<LivingEntity> getShipEntityCollisions(Level level,
                                                             AABBdc shipBox) {
        return level.getEntitiesOfClass(
                LivingEntity.class, toMcAABB(shipBox), LivingEntity::isAlive);
    }

    public static AABB toMcAABB(AABBdc jomlAABB) {
        return new AABB(
                jomlAABB.minX(), jomlAABB.minY(), jomlAABB.minZ(),
                jomlAABB.maxX(), jomlAABB.maxY(), jomlAABB.maxZ());
    }

    public static EarthController getOrCreate(LoadedServerShip ship, LivingEntity entity) {
        EarthController existing = ship.getAttachment(EarthController.class);
        if (existing != null) {
            return existing;
        } else {
            EarthController control = new EarthController();
            control.ship = ship;
            control.entity = entity;
            ship.setAttachment(EarthController.class, control);
            return control;
        }
    }
}