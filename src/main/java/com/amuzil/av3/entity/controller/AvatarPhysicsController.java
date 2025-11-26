package com.amuzil.av3.entity.controller;

import com.amuzil.av3.bending.element.Element;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.EntityGrid;
import com.amuzil.av3.entity.api.modules.IAvatarController;
import com.amuzil.av3.entity.construct.AvatarConstruct;
import com.amuzil.av3.entity.construct.AvatarElementCollider;
import com.amuzil.magus.physics.core.ForceCloud;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.HashMap;

// Should hold element rigidbodies and a force cloud...
public class AvatarPhysicsController extends AvatarConstruct implements IAvatarController {


    // Physics is automatically handled.
    private ForceCloud forceCloud;
    private HashMap<Integer, AvatarElementCollider> elements;
    private EntityGrid<AvatarElementCollider> elementGrid;

    public AvatarPhysicsController(EntityType<? extends AvatarConstruct> entityType, Level level) {
        super(entityType, level);
        setCollidable(false);
        setDamageable(false);
        setInvulnerable(true);
    }


    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     *
     * @param compound
     */
    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    public ForceCloud forceCloud() {
        return forceCloud;
    }

    @Override
    public void setForceCloud(ForceCloud cloud) {
        this.forceCloud = cloud;
    }

    @Override
    public void kill() {
        if (forceCloud != null) {
            // Should automatically remove itself from the physics space.
            forceCloud.kill();
        }
        elementGrid.clear();
        elements.clear();
        super.kill();
    }

    public EntityGrid<AvatarElementCollider> entityGrid() {
        return this.elementGrid;
    }
}
