package com.amuzil.omegasource.entity;

import com.amuzil.omegasource.api.magus.skill.traits.DataTrait;
import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.bending.element.Elements;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class AvatarEntity extends Entity {

    private static final EntityDataAccessor<Optional<UUID>> OWNER_ID =
            SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<String> ELEMENT=
            SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.STRING);

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

    public void setOwner(Entity owner) {
        this.owner = owner;
        this.entityData.set(OWNER_ID, Optional.of(owner.getUUID()));
    }

    public Entity owner() {
        if (this.owner == null) {
            Optional<UUID> uid = this.entityData.get(OWNER_ID);
            uid.ifPresent(value -> this.owner = ((ServerLevel) this.level()).getEntity(value));
        }
        return this.owner;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OWNER_ID, Optional.empty());
        this.entityData.define(ELEMENT, Elements.FIRE.id().toString());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     *
     * @param pCompound
     */
    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.hasUUID("OwnerUUID")) {
            this.entityData.set(OWNER_ID, Optional.of(pCompound.getUUID("OwnerUUID")));
        }

        // Element
        if (pCompound.contains("Element")) {
            this.element = Elements.get(ResourceLocation.parse(pCompound.getString("Element")));
            this.entityData.set(ELEMENT, element.id().toString());
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        this.entityData.get(OWNER_ID).ifPresent(uuid -> pCompound.putUUID("OwnerUUID", uuid));

        // Element
        if (element != null) {
            pCompound.putString("Element", element.name());
        }

    }
}
