package com.amuzil.av3.entity.projectile;

import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.entity.api.IHasHealth;
import com.amuzil.av3.renderer.sdf.IHasSDF;
import com.amuzil.av3.renderer.sdf.SignedDistanceFunction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class AvatarWaterBoundProjectile extends AvatarBoundProjectile implements IHasSDF, IHasHealth {

    private static final EntityDataAccessor<Float> HEALTH = SynchedEntityData.defineId(AvatarWaterBoundProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> MAX_HEALTH = SynchedEntityData.defineId(AvatarWaterBoundProjectile.class, EntityDataSerializers.FLOAT);

    public AvatarWaterBoundProjectile(EntityType<AvatarWaterBoundProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
    }

    public AvatarWaterBoundProjectile(Level pLevel) {
        this(AvatarEntities.AVATAR_WATER_BOUND_PROJECTILE_ENTITY_TYPE.get(), pLevel);
    }

    /**
     * Root SDF in entity-local space. Animate inside this impl if you want.
     */
    @Override
    public SignedDistanceFunction rootSDF() {
        return null;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HEALTH, 0f);
        builder.define(MAX_HEALTH, -1f);
    }

    /**
     * Gets the current health of this entity. Should be non-negative.
     */
    @Override
    public float health() {
        return entityData.get(HEALTH);
    }

    /**
     * Sets the current health of this entity. Should be non-negative.
     *
     * @param health
     */
    @Override
    public void health(float health) {
        entityData.set(HEALTH, health);
    }

    /**
     * Gets the maximum health of this entity. Should be positive.
     */
    @Override
    public float maxHealth() {
        return entityData.get(MAX_HEALTH);
    }

    /**
     * Sets the maximum health of this entity. Should be positive.
     *
     * @param maxHealth
     */
    @Override
    public void maxHealth(float maxHealth) {
        entityData.set(MAX_HEALTH, maxHealth);
    }

    @Override
    public void hurt(float damage) {
        health(health() - damage);
    }

    @Override
    public void heal(float amount) {
        health(health() + amount);
    }

    @Override
    public boolean noHealth() {
        return health() <= 0;
    }

    /**
     * If max health is -1, then we don't want things to affect this.
     *
     * @return
     */
    @Override
    public boolean hurtable() {
        return maxHealth() > -1;
    }
}
