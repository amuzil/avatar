package com.amuzil.caliber.physics.bullet.collision.body;

import com.amuzil.caliber.physics.bullet.collision.body.shape.MinecraftShape;
import com.amuzil.caliber.physics.bullet.collision.space.MinecraftSpace;
import net.minecraft.world.entity.player.Player;

/**
 * Used in the Force physics API
 */
public class ForceRigidBody extends ElementRigidBody{
    private Player priorityPlayer;
    private boolean dirtyProperties = true;


    public ForceRigidBody(ForcePhysicsElement element, MinecraftSpace space, MinecraftShape shape) {
        this(element, space, shape, 10.0f, 0.25f, 1.0f, 0.5f);
    }

    public ForceRigidBody(ForcePhysicsElement element, MinecraftSpace space, MinecraftShape shape, float mass, float dragCoefficient, float friction, float restitution) {
        super(element, space, shape, mass, dragCoefficient, friction, restitution);
    }
}
