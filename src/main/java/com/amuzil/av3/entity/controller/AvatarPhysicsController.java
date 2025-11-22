package com.amuzil.av3.entity.controller;

import com.amuzil.av3.bending.element.Element;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.modules.IAvatarController;
import com.amuzil.magus.physics.core.ForceCloud;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

// Should hold element rigidbodies and a force cloud...
public class AvatarPhysicsController extends AvatarEntity implements IAvatarController {


    // Physics is automatically handled.
    private ForceCloud forceCloud;

    // We ideally
    public AvatarPhysicsController(EntityType<?> entityType, Level level) {
        super(entityType, level);

        // This should never be collidable. Physics boolean can be used to determine if physics is enabled.
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
        super.kill();
    }
}
