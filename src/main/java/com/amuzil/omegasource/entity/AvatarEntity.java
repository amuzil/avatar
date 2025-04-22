package com.amuzil.omegasource.entity;

import com.amuzil.omegasource.api.magus.skill.traits.DataTrait;
import com.amuzil.omegasource.bending.element.Element;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class AvatarEntity extends Entity {

    private Entity owner;
    private Element element;

    private List<DataTrait> traits;

    // Data Sync for Owner
    // Data Sync for Element
    // Data Sync for Behaviour
    // Data Sync for each trait

    public AvatarEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        // entity.attachTraits( {
            // addTrait(lifetimeTrait);
        // });
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     *
     * @param pCompound
     */
    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }
}
