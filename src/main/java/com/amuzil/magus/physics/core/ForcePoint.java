package com.amuzil.magus.physics.core;

import com.amuzil.caliber.api.PhysicsElement;
import com.amuzil.caliber.physics.bullet.collision.body.ElementRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.ForceRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.shape.MinecraftShape;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * General force particle element storing 5 vectors:
 * Current position, previous position,
 * current velocity, previous velocity,
 * and force. Force is used to construct a vector field with other force points.
 * Force is separate from velocity.
 */
public class ForcePoint extends ForceElement {
    private final int lifetime = -1;
    private ElementRigidBody rigidBody;

    // Size is default 15 (5 vectors x 3 variables for 3d space. We're not using 4d vectors here yet...)
    public ForcePoint(int size, int type, Vec3 pos, Vec3 vel, Vec3 force, String id) {
        super(size, type, id);
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
//        this.rigidBody = new ElementRigidBody() {
//        }

    }

    public ForcePoint(int type, Vec3 pos, Vec3 vel, Vec3 force) {
        this(15, type, pos, vel, force, "");
    }

    public ForcePoint(int type, Vec3 pos, Vec3 vel, Vec3 force, String id) {
        this(15, type, pos, vel, force, id);
    }


    public int lifetime() {
        return this.lifetime;
    }

    public void addForce(Vec3 f) {
        Vec3 currentForce = get(2);
        insert(currentForce.add(f), 2);
    }


    /**
     * For generating a new {@link MinecraftShape.Convex}.
     *
     * @return the newly created {@link MinecraftShape.Convex}
     */
    @Override
    public MinecraftShape.Convex createShape() {
        return null;
        // return MinecraftShape.convex();
    }


    /**
     * Gets {@link ElementRigidBody} object associated with this element. You should
     * create and store this in your {@link PhysicsElement} implementation in the
     * constructor. You're able to set up the attributes and settings of your rigid
     * body however you like that way.
     *
     * @return the {@link ElementRigidBody}
     */
    @Override
    public @Nullable ForceRigidBody getRigidBody() {
        return null;
    }
}
