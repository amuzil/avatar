package com.amuzil.magus.physics.core;

import com.amuzil.magus.physics.constraints.Constraints;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public abstract class PhysicsElement implements IPhysicsElement {
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
    private double mass = 1;
    private double damping;
    private boolean surface = false;
    protected long seed = Seeds.fromUuid(UUID.randomUUID());

    public PhysicsElement(int type) {
        this(15, type);
    }

    public PhysicsElement(int size, int type) {
        this.data = new double[size];
        type(type);
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

    public String id() {
        return this.id;
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

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hash tables such as those provided by
     * {@link HashMap}.
     * <p>
     * The general contract of {@code hashCode} is:
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during
     *     an execution of a Java application, the {@code hashCode} method
     *     must consistently return the same integer, provided no information
     *     used in {@code equals} comparisons on the object is modified.
     *     This integer need not remain consistent from one execution of an
     *     application to another execution of the same application.
     * <li>If two objects are equal according to the {@link
     *     equals(Object) equals} method, then calling the {@code
     *     hashCode} method on each of the two objects must produce the
     *     same integer result.
     * <li>It is <em>not</em> required that if two objects are unequal
     *     according to the {@link equals(Object) equals} method, then
     *     calling the {@code hashCode} method on each of the two objects
     *     must produce distinct integer results.  However, the programmer
     *     should be aware that producing distinct integer results for
     *     unequal objects may improve the performance of hash tables.
     * </ul>
     *
     * @return a hash code value for this object.
     * @implSpec As far as is reasonably practical, the {@code hashCode} method defined
     * by class {@code Object} returns distinct integers for distinct objects.
     * @see Object#equals(Object)
     * @see System#identityHashCode
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id());// + Objects.hashCode(pos());
    }
}
