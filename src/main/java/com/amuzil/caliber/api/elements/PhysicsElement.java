package com.amuzil.caliber.api.elements;

import com.amuzil.caliber.api.elements.rigid.EntityRigidPhysicsElement;
import com.amuzil.caliber.physics.bullet.collision.body.rigidbody.ElementRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.shape.MinecraftShape;
import com.amuzil.caliber.physics.bullet.collision.space.MinecraftSpace;
import com.jme3.bullet.objects.PhysicsBody;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * This is the main interface you'll want to implement into your physics object.
 * It provides the basic components that a {@link PhysicsElement} needs in order
 * to behave properly in the {@link MinecraftSpace}.
 * 
 * @since 1.0.0
 */
public interface PhysicsElement<T> {

    /**
     * Gets {@link ElementRigidBody} object associated with this element. You should
     * create and store this in your {@link PhysicsElement} implementation in the
     * constructor. You're able to set up the attributes and settings of your rigid
     * body however you like that way.
     * 
     * @return the {@link ElementRigidBody}
     */
    @Nullable
    PhysicsBody getPhysicsBody();

    /**
     * For generating a new {@link MinecraftShape.Convex}.
     * 
     * @return the newly created {@link MinecraftShape.Convex}
     */
    MinecraftShape.Convex createShape();

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