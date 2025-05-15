package com.amuzil.omegasource.utils.physics.core;

import com.amuzil.omegasource.utils.physics.constraints.Constraints;
import net.minecraft.world.phys.Vec3;

public abstract class PhysicsElement implements IPhysicsElement {
    public double[] data;

    /**
     * Contains important information about the ForcePoint.
     * What state is it? Gas, Liquid, Solid (first element)? What constraints does it have?
     * See the Constraints class for a list of them.
     */

    public byte[] header = new byte[Constraints.HEADER_LENGTH];
    private int id;
    private double mass;
    private double damping;

    public PhysicsElement(int type) {
        this(15, type);
    }

    public PhysicsElement(int size, int type) {
        this.data = new double[size];
        type(type);
    }

    @Override
    public byte[] header() {
        return header;
    }

    @Override
    public double[] data() {
        return data;
    }

    public int id() {
        return this.id;
    }

    public void id(int id) {
        this.id = id;
    }

    public void mass(double mass) {
        this.mass = mass;
    }

    @Override
    public double mass() {
        return mass;
    }

    public void damping(double damping) {
        this.damping = damping;
    }

    @Override
    public double damping() {
        return damping;
    }

    @Override
    public Vec3 pos() {
        return get(0);
    }

    @Override
    public Vec3 prevPos() {
        return get(1);
    }

    @Override
    public Vec3 vel() {
        return get(2);
    }

    @Override
    public Vec3 prevVel() {
        return get(3);
    }

    @Override
    public Vec3 force() {
        return get(4);
    }
}
