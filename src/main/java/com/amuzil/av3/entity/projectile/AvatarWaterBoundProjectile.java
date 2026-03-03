package com.amuzil.av3.entity.projectile;

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

    public AvatarWaterBoundProjectile(EntityType<AvatarBoundProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
    }

    /**
     * Root SDF in entity-local space. Animate inside this impl if you want.
     */
    @Override
    public SignedDistanceFunction rootSDF() {
        return null;
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
}
