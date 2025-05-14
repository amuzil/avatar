package com.amuzil.omegasource.utils.physics;

import net.minecraft.world.phys.Vec3;

/**
 * General force particle element storing 5 vectors:
 * Current position, previous position,
 * current velocity, previous velocity,
 * and force. Force is used to construct a vector field with other force points.
 * Force is separate from velocity.
 */
public class ForcePoint {

    private double[] data;
    private int id;
    private int lifetime = - 1;
    // Custom flags and information.
    // Type first byte. 0 = gas, 1 = water, 2 = solid.
    // Second byte flags such as constraints, etc
    private byte[] header;
    private double mass;
    private double damping;


    public ForcePoint(double[] data) {
        this.data = data;
    }

    // Size is default 15 (5 vectors x 3 variables for 3d space. We're not using 4d vectors here yet...)
    public ForcePoint(int size, Vec3 pos, Vec3 vel, Vec3 force) {
        this.data = new double[15];
        insert(pos, 0);
        // Prev pos
        insert(Vec3.ZERO, 1);
        insert(vel, 2);
        // Prev force
        insert(Vec3.ZERO, 3);
        // Direction
        insert(force, 4);
    }

    public ForcePoint(Vec3 pos, Vec3 vel, Vec3 force) {
        this(15, pos, vel, force);
    }

    // This is mutable. You have been warned. Use this power wisely.
    public double[] data() {
        return this.data;
    }

    public void insert(Vec3 vec, int column) {
        this.data[column * 3] = vec.x();
        this.data[1 + column * 3] = vec.y();
        this.data[2 + column * 3] = vec.z();
    }

    public Vec3 get(int column) {
        return new Vec3(data[column * 3], data[1 + column * 3], data[2 + column * 3]);
    }
    public Vec3 pos() {
        return get(0);
    }

    public Vec3 prevPos() {
        return get(1);
    }

    public Vec3 vel() {
        return get(2);
    }

    public Vec3 prevVel() {
        return get(3);
    }

    public Vec3 force() {
        return get(4);
    }

    public int id() {
        return this.id;
    }

    public int lifetime() {
        return this.lifetime;
    }

    public byte[] header() {
        return this.header;
    }

    public double mass() {
        return this.mass;
    }

    public double damping() {
        return this.damping;
    }


}
