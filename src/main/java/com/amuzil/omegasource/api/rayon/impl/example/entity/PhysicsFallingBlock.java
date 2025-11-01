package com.amuzil.omegasource.api.rayon.impl.example.entity;

import com.amuzil.omegasource.api.rayon.api.EntityPhysicsElement;
import com.amuzil.omegasource.api.rayon.impl.bullet.collision.body.EntityRigidBody;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;


public class PhysicsFallingBlock extends FallingBlockEntity implements EntityPhysicsElement {
	private final EntityRigidBody rigidBody;
    private static final EntityDataAccessor<Float> WIDTH = SynchedEntityData.defineId(PhysicsFallingBlock.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(PhysicsFallingBlock.class, EntityDataSerializers.FLOAT);

    public PhysicsFallingBlock(EntityType<? extends FallingBlockEntity> type, Level level) {
		super(type, level);
		this.rigidBody = new EntityRigidBody(this);
	}

    public void setWidth(float width) {
        entityData.set(WIDTH, width);
    }

    public void setHeight(float height) {
        entityData.set(HEIGHT, height);
    }

    public float width() {
        return entityData.get(WIDTH);
    }

    public float height() {
        return entityData.get(HEIGHT);
    }

    public AABB getSize() {
        return new AABB(xo - width() / 2, yo - height() / 2, zo - width() / 2, xo + width() / 2, yo + height() / 2, zo + width() / 2);
    }

	@Override
	public @Nullable EntityRigidBody getRigidBody() {
		return this.rigidBody;
	}

    @Override
    protected AABB makeBoundingBox() {
        return getSize();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HEIGHT, 1.0f);
        this.entityData.define(WIDTH, 1.0f);
    }
}
