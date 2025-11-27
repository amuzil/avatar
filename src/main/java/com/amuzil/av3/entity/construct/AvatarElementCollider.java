package com.amuzil.av3.entity.construct;

import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.caliber.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.shape.MinecraftShape;
import com.amuzil.caliber.physics.bullet.math.Convert;
import com.jme3.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;


// Used for fire, water, and air. RigidBlock is earth.
public class AvatarElementCollider extends AvatarRigidBlock {

    private static final EntityDataAccessor<Optional<UUID>> SPAWNER_ID = SynchedEntityData.defineId(AvatarElementCollider.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> RESET = SynchedEntityData.defineId(AvatarElementCollider.class, EntityDataSerializers.BOOLEAN);
    private Entity spawner;

    public AvatarElementCollider(EntityType<? extends AvatarRigidBlock> type, Level level) {
        super(AvatarEntities.AVATAR_ELEMENT_COLLIDER_ENTITY_TYPE.get(), level);
    }

    public AvatarElementCollider(Level level) {
        super(AvatarEntities.AVATAR_ELEMENT_COLLIDER_ENTITY_TYPE.get(), level);
    }


    @Override
    public void kill() {
        super.kill();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SPAWNER_ID, Optional.empty());
        builder.define(RESET, false);
    }

    public void spawner(@NotNull Entity spawner) {
        this.spawner = spawner;
        this.entityData.set(SPAWNER_ID, Optional.of(spawner.getUUID()));
    }

    public Entity spawner() {
        if (this.spawner == null) {
            Optional<UUID> uid = this.entityData.get(SPAWNER_ID);
            uid.ifPresent(value -> {
                if (!level().isClientSide())
                    this.spawner = ((ServerLevel) this.level()).getEntity(value);
            });
        }
        return this.spawner;
    }

    public boolean reset() {
        return this.entityData.get(RESET);
    }

    public void reset(boolean reset) {
        this.entityData.set(RESET, reset);
    }

    public void resetPhysics() {
        this.getRigidBody().setKinematic(true);
        this.getRigidBody().setGravity(Vector3f.ZERO);
        this.getRigidBody().clearForces();
        this.getRigidBody().setLinearVelocity(Vector3f.ZERO);
        this.getRigidBody().setAngularVelocity(Vector3f.ZERO);
    }

    public void resetPos(Vec3 pos) {
        this.setPos(pos);
        this.getRigidBody().setPhysicsLocation(Convert.toBullet(pos));
        this.setStartPos(new BlockPos((int) pos.x, (int) pos.y, (int) pos.z));
    }

    @Override
    public void tick() {
        super.tick();
        this.getRigidBody().setCollisionShape(MinecraftShape.box(getBoundingBox()));
        if (isControlled())
            control(0.5f);
    }

    @Override
    public @Nullable EntityRigidBody getRigidBody() {
        return super.getRigidBody();
    }
}
