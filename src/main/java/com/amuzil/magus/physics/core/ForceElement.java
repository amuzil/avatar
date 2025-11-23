package com.amuzil.magus.physics.core;

import com.amuzil.caliber.physics.bullet.collision.body.ForceRigidBody;
import com.amuzil.caliber.physics.bullet.math.Convert;
import com.amuzil.magus.physics.constraints.Constraints;
import com.jme3.math.Vector3f;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

// We want these to count as PhysicsElements from the physics engine's perspective, not just my own custom implementation.
public abstract class ForceElement implements ForcePhysicsElement {
    public double[] data;

    /**
     * Contains important information about the ForcePoint.
     * What state is it? Gas, Liquid, Solid (first element)? What constraints does it have?
     * See the Constraints class for a list of them.
     */

    public byte[] header = new byte[Constraints.HEADER_LENGTH];
    int maxLifetime = -1;
    int timeExisted = 0;
    private String id;
    private String ownerId;
    private double mass = 1;
    private double damping;
    private boolean surface = false;
    protected long seed = Seeds.fromUuid(UUID.randomUUID());
    private ForceRigidBody rigidBody;

    public ForceElement(int type, String id) {
        this(15, type, id);
    }

    public ForceElement(int size, int type, String id) {
        this.data = new double[size];
        type(type);
        this.id = id;
    }

    public void syncFromPhysics() {
        pos(Convert.toVec3(rigidBody.getPhysicsLocation(new Vector3f())));
    }

    // Override these if you don't want your data stored in the double array sa per these defaults.
    public void pos(Vec3 pos) {
        insert(pos, 0);
    }

    public void prevPos(Vec3 pos) {
        insert(pos, 1);
    }

    public void vel(Vec3 vel) {
        insert(vel, 2);
    }

    public void prevVel(Vec3 vel) {
        insert(vel, 3);
    }

    public void force(Vec3 force) {
        insert(force, 4);
    }

    @Override
    public Vec3 newVel(double dt, float mass) {
        return vel().add(force().scale(dt));
    }

    @Override
    public Vec3 newPos(double dt) {
        return pos().add(vel().scale(dt));
    }

    public int maxLife() {
        return this.maxLifetime;
    }

    public int timeExisted() {
        return this.timeExisted;
    }

    public boolean surface() {
        return this.surface;
    }

    public void surface(boolean surface) {
        this.surface = surface;
    }

    @Override
    public byte[] header() {
        return header;
    }

    @Override
    public double[] data() {
        return data;
    }

    // Immutability is important.
    @Override
    public void data(double[] data) {
        this.data = data.clone();
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String ownerId() {
        return ownerId;
    }

    public void owner(String uuid) {
        this.ownerId = uuid;
    }

    public void id(String id) {
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
    public long seed() {
        return seed;
    }

    // Aceleration / Force
    @Override
    public Vec3 force() {
        return get(4);
    }
}
