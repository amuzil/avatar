package com.amuzil.magus.physics.core;

import net.minecraft.world.phys.Vec3;

/**
 * General force particle element storing 5 vectors:
 * Current position, previous position,
 * current velocity, previous velocity,
 * and force. Force is used to construct a vector field with other force points.
 * Force is separate from velocity.
 */
public class ForcePoint extends PhysicsElement {
    private final int lifetime = -1;
    // Size is default 15 (5 vectors x 3 variables for 3d space. We're not using 4d vectors here yet...)
    public ForcePoint(int size, int type, Vec3 pos, Vec3 vel, Vec3 force) {
        super(size, type);
        //Current Pos
        insert(pos, 0);
        // Prev pos
        insert(Vec3.ZERO, 1);
        // Current vel
        insert(vel, 2);
        // Prev force
        insert(Vec3.ZERO, 3);
        // Direction / Force / Acceleration
        insert(force, 4);

    }

    public ForcePoint(int type, Vec3 pos, Vec3 vel, Vec3 force) {
        this(15, type, pos, vel, force);
    }


    public int lifetime() {
        return this.lifetime;
    }

    public void addForce(Vec3 f) {
        Vec3 currentForce = get(2);
        insert(currentForce.add(f), 2);
    }

}
