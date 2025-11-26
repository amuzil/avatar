package com.amuzil.av3.entity.construct;

import com.amuzil.av3.entity.AvatarEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;


// Used for fire, water, and air. RigidBlock is earth.
public class AvatarElementCollider extends AvatarRigidBlock {

    private static final EntityDataAccessor<Optional<UUID>> SPAWNER_ID = SynchedEntityData.defineId(AvatarElementCollider.class, EntityDataSerializers.OPTIONAL_UUID);

    private Entity spawner;

    public AvatarElementCollider(EntityType<? extends AvatarRigidBlock> type, Level level) {
        super(type, level);
    }

    public AvatarElementCollider(Level level) {
        super(level);
    }


    @Override
    public void kill() {
        super.kill();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SPAWNER_ID, Optional.empty());
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
}
