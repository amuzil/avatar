package com.amuzil.caliber.physics.bullet.collision.body;

import com.amuzil.caliber.api.EntityPhysicsElement;
import com.amuzil.caliber.api.PlayerPhysicsElement;
import com.amuzil.caliber.physics.bullet.collision.body.shape.MinecraftShape;
import com.amuzil.caliber.physics.bullet.collision.space.MinecraftSpace;
import com.jme3.math.Vector3f;
import net.minecraft.world.entity.player.Player;

public class PlayerRigidBody extends ElementRigidBody {
    private final Player player;
    private boolean dirtyProperties = true;

    public PlayerRigidBody(PlayerPhysicsElement element, MinecraftSpace space, MinecraftShape shape, float mass, float dragCoefficient, float friction, float restitution) {
        super(element, space, shape, mass, dragCoefficient, friction, restitution);
        player = (Player) element;
    }

    public PlayerRigidBody(PlayerPhysicsElement element, MinecraftSpace space, MinecraftShape shape) {
        this(element, space, shape, 10.0f, 0.25f, 1.0f, 0.5f);
    }

    /**
     * The simplest way to create a new {@link PlayerRigidBody}.
     *
     * @param element the element to base this body around
     */
    public PlayerRigidBody(PlayerPhysicsElement element) {
        this(element, MinecraftSpace.get(element.cast().level()), element.createShape());
    }

    @Override
    public PlayerPhysicsElement getElement() {
        return (PlayerPhysicsElement) super.getElement();
    }

    public Player getPlayer() {
        return this.player;
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