package com.amuzil.omegasource.utils.ship;

import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.entity.projectile.AvatarProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBdc;
import org.valkyrienskies.core.api.ships.*;
import org.valkyrienskies.core.api.world.PhysLevel;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;


public final class EarthController implements ShipPhysicsListener {
    private final ConcurrentLinkedQueue<Vector3dc> invForces = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Vector3dc> invTorques = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Vector3dc> rotForces = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Vector3dc> rotTorques = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<InvForceAtPos> invPosForces = new ConcurrentLinkedQueue<>();

    private volatile boolean isControlled = false;
    private volatile boolean toBeStatic = false;
    private volatile boolean toBeStaticUpdated = false;
    private ServerShip ship;
    private LivingEntity entity;
    private Bender bender;
    private OriginalBlocks originalBlocks;
    public final AtomicInteger tickCount = new AtomicInteger(0);
    public final AtomicInteger idleTickCount = new AtomicInteger(0);

    @Override
    public void physTick(@NotNull PhysShip physShip, @NotNull PhysLevel physLevel) {

//        if (!invForces.isEmpty()) {
//            if (physShip.isStatic())
//                physShip.setStatic(false);
//            Vector3dc force = invForces.poll();
//            if (tickCount.get() >= 5) {
//                double yForce = force.y() / 10;
//                if (physShip.getVelocity().y() <= 0) {
//                    yForce += 7000;
//                } else {
//                    yForce = 0;
//                }
//                Vector3d newForce = new Vector3d(0, yForce, 0);
////                Avatar.LOGGER.info("applyForces: {} {}", yForce, physShip.getVelocity().y());
//                physShip.applyInvariantForce(newForce);
//            } else {
//                physShip.applyInvariantForce(force);
//            }
//        }
        if (!invForces.isEmpty())
            physShip.applyInvariantForce(invForces.poll());
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

        checkCollision(physShip);

        if (idleTickCount.get() > 400) {
            cleanUpShip();
        }
    }

    private void checkCollision(PhysShip physShip) {
        ServerLevel level = (ServerLevel) entity.level();
        Vector3dc velocity = ship.getVelocity();
        double mag = velocity.length();
        boolean isMoving = mag > 0.1;
        boolean isMovingFast = mag > 2.0;
        idleTickCount.incrementAndGet();
        checkShipProjectileCollisions(level, ship, physShip);
        if (isMoving || isControlled) {
            tickCount.incrementAndGet(); idleTickCount.set(0);
            if (isMovingFast)
                checkShipShipCollisions(level, ship);
            checkShipEntityCollisions(level, ship);
        }
    }

//    private void cleanUpShip() {
//        if (originalBlocks == null) return;
//        ServerLevel level = (ServerLevel) entity.level();
//        originalBlocks.get().forEach(block -> {
//            Vector3dc shipYardPos = ship.getTransform().getPositionInShip();
//            BlockPos shipBlockPos = BlockPos.containing(VectorConversionsMCKt.toMinecraft(shipYardPos));
//            level.destroyBlock(shipBlockPos, false);
//            level.setBlock(block.pos(), block.state(), 3);
//        });
//        bender.getSelection().originalBlocksMap().remove(ship.getId());
//    }

    private void cleanUpShip() {
        if (originalBlocks == null || ship == null) return;
        ServerLevel level = (ServerLevel) entity.level();
        // --- 1. Fetch ship transform and current position ---
        var shipTransform = ship.getTransform();
        Vector3dc shipPos = shipTransform.getPositionInWorld();

        // --- 2. For each stored block in this ship’s originalBlocks ---
        originalBlocks.get().forEach(block -> {
//                Vector3dc localPos = (Vector3dc) VectorConversionsMCKt.toJOML(block.pos());
//                Vector3dc worldBlockPosVec = shipTransform.getShipToWorld().transformPosition(localPos, new org.joml.Vector3d());
//                BlockPos worldBlockPos = BlockPos.containing(VectorConversionsMCKt.toMinecraft(worldBlockPosVec));
//
////             --- 3. Destroy the block currently inside the ship world ---
//                if (level.getBlockState(worldBlockPos).getBlock() != Blocks.AIR)
//                    level.destroyBlock(worldBlockPos, false);

            // --- 4. Restore original block back into the world ---
            level.setBlock(block.pos(), block.state(), 3);
        });

        // --- 5. Remove the ship from the world so VS stops simulating it ---
        try {
            if (ship != null)
                VSGameUtilsKt.getShipObjectWorld(level).deleteShip(ship);
        } catch (IllegalArgumentException e) {
//            System.err.println("Failed to delete, ship not found!");
        }

        // --- 6. Clear stored mapping for that ship ID ---
        bender.getSelection().originalBlocksMap().remove(ship.getId());
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

    public void setControlled(boolean controlled) {
        isControlled = controlled;
    }

    public boolean isControlled() {
        return isControlled;
    }

    private record InvForceAtPos(Vector3dc force, Vector3dc pos) {}

    public void checkShipShipCollisions(ServerLevel level, ServerShip ship) {
        String dimensionId = VSGameUtilsKt.getDimensionId(level);
        VSGameUtilsKt.getShipObjectWorld(level).getAllShips().getIntersecting(
                ship.getWorldAABB(),
                dimensionId
        ).forEach(serverShip -> {
            if (serverShip.getId() != ship.getId()) {
                bender.startTickingOriginalBlocks(serverShip.getId());
                Vector3dc shipYardPos = serverShip.getTransform().getPositionInShip();
                BlockPos blockPos = BlockPos.containing(VectorConversionsMCKt.toMinecraft(shipYardPos));
                level.destroyBlock(blockPos, false);
            }
        });
    }

    public void checkShipEntityCollisions(ServerLevel level, ServerShip ship) {
        getShipEntityCollisions(level, ship.getWorldAABB()).forEach(entity -> {
            if (entity != null && entity != this.entity) {
                bender.startTickingOriginalBlocks(ship.getId());
                Vector3dc shipYardPos = ship.getTransform().getPositionInShip();
                BlockPos blockPos = BlockPos.containing(VectorConversionsMCKt.toMinecraft(shipYardPos));
                level.destroyBlock(blockPos, false);
                level.destroyBlock(blockPos.north(), false);
                level.destroyBlock(blockPos.south(), false);
                level.destroyBlock(blockPos.east(), false);
                level.destroyBlock(blockPos.west(), false);
//                System.out.println("EarthController: COLLIDED WITH ENTITY " + blockPos);
                Vec3 motion = VectorConversionsMCKt.toMinecraft(ship.getVelocity());
                entity.addDeltaMovement(motion.scale(0.05));
                entity.hasImpulse = true; entity.hurtMarked = true;
                entity.hurt(entity.damageSources().thrown(entity, entity), 4f);
            }
        });
    }

    public void checkShipProjectileCollisions(ServerLevel level, ServerShip ship, PhysShip physShip) {
        getShipProjectileCollisions(level, ship.getWorldAABB()).forEach(entity -> {
            if (entity != null) {
                bender.startTickingOriginalBlocks(ship.getId());
                Vector3dc shipYardPos = ship.getTransform().getPositionInShip();
                BlockPos blockPos = BlockPos.containing(VectorConversionsMCKt.toMinecraft(shipYardPos));
                double mass = ship.getInertiaData().getMass();
                Vec3 vec3 = entity.getDeltaMovement().normalize()
                        .multiply(250*mass, 250*mass, 250*mass);
                Vector3d v3d = VectorConversionsMCKt.toJOML(vec3);
                physShip.applyInvariantForce(v3d);
                Vec3 motion = VectorConversionsMCKt.toMinecraft(ship.getVelocity());
                entity.addDeltaMovement(motion.scale(0.025));
                entity.hasImpulse = true; entity.hurtMarked = true;
            }
        });
    }

    public static List<LivingEntity> getShipEntityCollisions(Level level, AABBdc shipBox) {
        return level.getEntitiesOfClass(
                LivingEntity.class, toMcAABB(shipBox), LivingEntity::isAlive);
    }

    public static List<AvatarProjectile> getShipProjectileCollisions(Level level, AABBdc shipBox) {
        return level.getEntitiesOfClass(
                AvatarProjectile.class, toMcAABB(shipBox), AvatarProjectile::isAlive);
    }

    public static AABB toMcAABB(AABBdc jomlAABB) {
        return new AABB(
                jomlAABB.minX(), jomlAABB.minY(), jomlAABB.minZ(),
                jomlAABB.maxX(), jomlAABB.maxY(), jomlAABB.maxZ());
    }

    public static EarthController getOrCreate(LoadedServerShip ship, Bender bender) {
        EarthController existing = ship.getAttachment(EarthController.class);
        if (existing != null) {
            return existing;
        } else {
            EarthController control = new EarthController();
            control.ship = ship;
            control.bender = bender;
            control.entity = bender.getEntity();
            control.originalBlocks = bender.getSelection().originalBlocksMap().get(ship.getId());
            ship.setAttachment(control);
            return control;
        }
    }
}