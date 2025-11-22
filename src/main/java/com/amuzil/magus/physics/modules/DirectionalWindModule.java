package com.amuzil.magus.physics.modules;

import com.amuzil.magus.physics.core.ForceCloud;
import com.amuzil.magus.physics.core.ForcePoint;
import com.amuzil.magus.physics.core.IForceElement;
import net.minecraft.world.phys.Vec3;
import org.joml.Random;

public class DirectionalWindModule implements IPhysicsModule {

    private final Vec3 direction;
    private final double magnitude;
    private final double spread;
    Random random = new Random();

    public DirectionalWindModule(Vec3 direction, double magnitude, double spread) {
        this.direction = direction.normalize();
        this.magnitude = magnitude;
        this.spread = spread;
    }

    @Override
    public void preSolve(IForceElement element) {
        // or store one in module

        if (!(element instanceof ForceCloud))
            return;

        ForceCloud cloud = (ForceCloud) element;
        for (ForcePoint p : cloud.points()) {
            double rx = (random.nextFloat() * 2.0 - 1.0);
            double ry = (random.nextFloat() * 2.0 - 1.0);
            double rz = (random.nextFloat() * 2.0 - 1.0);
            Vec3 jitter = new Vec3(rx, ry, rz).normalize().scale(spread);

            Vec3 f = direction.add(jitter).normalize().scale(magnitude);
            p.addForce(f);
        }
    }

    @Override
    public void solve(IForceElement cloud) {
    }

    @Override
    public void postSolve(IForceElement cloud) {
    }

}
