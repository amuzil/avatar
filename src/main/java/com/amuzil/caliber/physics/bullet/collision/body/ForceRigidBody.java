package com.amuzil.caliber.physics.bullet.collision.body;

import com.amuzil.caliber.physics.bullet.collision.body.shape.MinecraftShape;
import com.amuzil.caliber.physics.bullet.collision.space.MinecraftSpace;
import com.amuzil.magus.physics.core.ForcePhysicsElement;
import com.jme3.math.Vector3f;
import net.minecraft.world.entity.player.Player;

/**
 * Used in the Force physics API
 */
public class ForceRigidBody extends ElementRigidBody {
    private Player priorityPlayer;
    private boolean dirtyProperties = true;


    public ForceRigidBody(ForcePhysicsElement element, MinecraftSpace space, MinecraftShape shape) {
        this(element, space, shape, 10.0f, 0.25f, 1.0f, 0.5f);
    }

    public ForceRigidBody(ForcePhysicsElement element, MinecraftSpace space, MinecraftShape shape, float mass, float dragCoefficient, float friction, float restitution) {
        super(element, space, shape, mass, dragCoefficient, friction, restitution);
    }

    @Override
    public ForcePhysicsElement getElement() {
        return (ForcePhysicsElement) super.getElement();
    }

    public Player getPriorityPlayer() {
        return this.priorityPlayer;
    }

    public boolean isPositionDirty() {
        return this.getFrame() != null && (this.getFrame().getLocationDelta(new Vector3f()).length() > 0.1f || this.getFrame().getRotationDelta(new Vector3f()).length() > 0.01f);
    }

    public boolean arePropertiesDirty() {
        return this.dirtyProperties;
    }

    public void setPropertiesDirty(boolean dirtyProperties) {
        this.dirtyProperties = dirtyProperties;
    }

    public void prioritize(Player priorityPlayer) {
        this.priorityPlayer = priorityPlayer;
        this.dirtyProperties = true;
    }

    @Override
    public void setMass(float mass) {
        super.setMass(mass);
        this.dirtyProperties = true;
    }

    @Override
    public void setDragCoefficient(float dragCoefficient) {
        super.setDragCoefficient(dragCoefficient);
        this.dirtyProperties = true;
    }

    @Override
    public void setFriction(float friction) {
        super.setFriction(friction);
        this.dirtyProperties = true;
    }

    @Override
    public void setRestitution(float restitution) {
        super.setRestitution(restitution);
        this.dirtyProperties = true;
    }

    @Override
    public void setTerrainLoadingEnabled(boolean doTerrainLoading) {
        super.setTerrainLoadingEnabled(doTerrainLoading);
        this.dirtyProperties = true;
    }
}
