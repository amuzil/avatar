package com.amuzil.caliber.api;

import com.amuzil.caliber.physics.bullet.collision.body.rigidbody.ElementRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.shape.MinecraftShape;
import com.amuzil.caliber.physics.bullet.collision.body.softbody.ElementSoftBody;
import com.amuzil.caliber.physics.bullet.collision.space.MinecraftSpace;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * This is the main interface you'll want to implement into your physics object.
 * It provides the basic components that a {@link SoftPhysicsElement} needs in order
 * to behave properly in the {@link MinecraftSpace}.
 * 
 * @since 1.0.0
 */
public interface SoftPhysicsElement<T> extends PhysicsElement<T> {

    /**
     * Gets {@link ElementSoftBody} object associated with this element. You should
     * create and store this in your {@link SoftPhysicsElement} implementation in the
     * constructor. You're able to set up the attributes and settings of your rigid
     * body however you like that way.
     * 
     * @return the {@link ElementSoftBody}
     */
    @Nullable
    ElementSoftBody getPhysicsBody();

    /**
     * For generating a new {@link MinecraftShape.Convex}.
     * 
     * @return the newly created {@link MinecraftShape.Convex}
     */
    MinecraftShape.Convex createShape();

    /**
     * Mainly used for lerping within your renderer.
     * 
     * @param store     any vector to store the output in
     * @param tickDelta the delta time between ticks
     * @return the lerped vector
     */
    default Vector3f getPhysicsLocation(Vector3f store, float tickDelta) {
        var rigidBody = this.getPhysicsBody();
        if (rigidBody == null)
            return new Vector3f();
        return rigidBody.getFrame().getLocation(store, tickDelta);
    }

    /**
     * Mainly used for lerping within your renderer.
     * 
     * @param store     the quaternion to store the output in
     * @param tickDelta the delta time between ticks
     * @return the "slerped" quaternion
     */
    default Quaternion getPhysicsRotation(Quaternion store, float tickDelta) {
        var rigidBody = this.getPhysicsBody();
        if (rigidBody == null)
            return new Quaternion();
        return rigidBody.getFrame().getRotation(store, tickDelta);
    }

    /**
     * Returns the object as its generic type. (e.g. {@link EntityRigidPhysicsElement} ->
     * {@link Entity})
     * 
     * @return this as {@link T}
     */
    @SuppressWarnings("unchecked")
    default T cast() {
        return (T) this;
    }
}