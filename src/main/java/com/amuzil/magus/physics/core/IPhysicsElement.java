package com.amuzil.magus.physics.core;

import com.amuzil.magus.physics.constraints.ConstraintUtils;
import com.amuzil.magus.physics.constraints.Constraints;
import net.minecraft.world.phys.Vec3;

public interface IPhysicsElement {

    byte[] header();

    double[] data();

    double mass();

    double damping();

    default int type() {
        return header()[Constraints.TYPE_INDEX];
    }

    long seed();

    default void type(int type) {
        header()[Constraints.TYPE_INDEX] = (byte) type;
    }

    default void conOn(Constraints.ConstraintType c) {
        ConstraintUtils.enableConstraint(header(), c);
    }

    default void conOff(Constraints.ConstraintType c) {
        ConstraintUtils.disableConstraint(header(), c);
    }

    default boolean constraint(Constraints.ConstraintType c) {
        return ConstraintUtils.hasConstraint(header(), c);
    }

    default void insert(Vec3 vec, int column) {
        data()[column * 3] = vec.x();
        data()[1 + column * 3] = vec.y();
        data()[2 + column * 3] = vec.z();
    }

    default Vec3 get(int column) {
        return new Vec3(data()[column * 3], data()[1 + column * 3], data()[2 + column * 3]);
    }

    Vec3 pos();

    Vec3 prevPos();

    Vec3 vel();

    Vec3 prevVel();

    Vec3 force();

    Vec3 newVel(double dt, float mass);

    Vec3 newPos(double dt);

}
