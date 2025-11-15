package com.amuzil.caliber.physics.bullet.collision.body.softbody;

import com.amuzil.caliber.api.PhysicsSynced;
import com.amuzil.caliber.api.elements.soft.EntitySoftPhysicsElement;
import com.amuzil.caliber.physics.bullet.collision.body.shape.MinecraftShape;
import com.amuzil.caliber.physics.bullet.collision.space.MinecraftSpace;
import com.jme3.math.Vector3f;
import net.minecraft.world.entity.player.Player;

public class EntitySoftBody extends ElementSoftBody implements PhysicsSynced {
    private Player priorityPlayer;
    private boolean dirtyProperties = true;

    public EntitySoftBody(EntitySoftPhysicsElement element, MinecraftSpace space, MinecraftShape shape, float mass, float dragCoefficient, float friction, float restitution) {
        super(element, space, shape, mass, dragCoefficient, friction, restitution);
    }

    public EntitySoftBody(EntitySoftPhysicsElement element, MinecraftSpace space, MinecraftShape shape) {
        this(element, space, shape, 10.0f, 0.25f, 1.0f, 0.5f);
    }

    /**
     * The simplest way to create a new {@link EntitySoftBody}.
     *
     * @param element the element to base this body around
     */
    public EntitySoftBody(EntitySoftPhysicsElement element) {
        this(element, MinecraftSpace.get(element.cast().level()), element.createShape());
    }

    @Override
    public EntitySoftPhysicsElement getElement() {
        return (EntitySoftPhysicsElement) super.getElement();
    }

    @Override
    public boolean isRigid() {
        return false;
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