package com.amuzil.omegasource.utils.ship;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;


public final class EarthController implements ShipForcesInducer {
    private ConcurrentLinkedQueue<Vector3dc> invForces = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Vector3dc> invTorques = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Vector3dc> rotForces = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Vector3dc> rotTorques = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<InvForceAtPos> invPosForces = new ConcurrentLinkedQueue<>();

    private volatile boolean toBeStatic = false;
    private volatile boolean toBeStaticUpdated = false;
    public final AtomicInteger tickCount = new AtomicInteger(0);

    public void applyForces(@NotNull PhysShip physShip) {
        if (!invForces.isEmpty()) {
            Vector3dc force = invForces.poll();
            if (tickCount.get() >= 4) {
                double yForce = force.y() - 24000.0D;
                if (physShip.getVelocity().y() > 0) {
                    yForce -= 500;
                } else {
                    yForce += 800;
                }
                Vector3d newForce = new Vector3d(0, yForce, 0);
                System.out.println("applyForces: " + yForce +  " " + physShip.getVelocity().y());
                physShip.applyInvariantForce(newForce);
            } else {
                System.out.println("EarthController.applyForces initial: " + force);
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

        tickCount.incrementAndGet();
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

    public static EarthController getOrCreate(LoadedServerShip ship) {
        EarthController existing = ship.getAttachment(EarthController.class);
        if (existing != null) {
            return existing;
        } else {
            EarthController control = new EarthController();
            ship.setAttachment(EarthController.class, control);
            return control;
        }
    }
}