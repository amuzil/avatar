package com.amuzil.omegasource.utils.physics;

import net.minecraft.world.phys.Vec3;

/**
 * General force particle element storing 5 vectors:
 * Current position, previous position,
 * current velocity, previous velocity,
 * and direction. Direction is used to construct a vector field with other force particles.
 * Direction is separate from force.
 */
public class ForceParticle {

    private double[][] data;

    public ForceParticle(double[][] data) {
        this.data = data;
    }

    public ForceParticle(Vec3 pos, Vec3 force, Vec3 dir) {
        this.data = new double[5][];
        insert(pos, 0);
        // Prev pos
        insert(Vec3.ZERO, 1);
        insert(force, 2);
        // Prev force
        insert(Vec3.ZERO, 3);
        // Direction
        insert(dir, 4);
    }

    public void insert(Vec3 vec, int column) {
        this.data[0][column] = vec.x();
        this.data[1][column] = vec.y();
        this.data[2][column] = vec.z();
    }

    public Vec3 get(int column) {
        return new Vec3(data[0][column], data[1][column], data[2][column]);
    }
    public Vec3 pos() {
        return get(0);
    }

    public Vec3 prevPos() {
        return get(1);
    }

    public Vec3 force() {
        return get(2);
    }

    public Vec3 prevForce() {
        return get(3);
    }

    public Vec3 dir() {
        return get(4);
    }
}
