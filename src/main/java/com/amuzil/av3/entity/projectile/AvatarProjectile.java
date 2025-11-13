package com.amuzil.av3.entity.projectile;

import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IAvatarProjectile;
import com.amuzil.av3.entity.api.IRenderModule;
import com.amuzil.av3.entity.modules.ModuleRegistry;
import com.amuzil.av3.entity.modules.entity.TimeoutModule;
import com.amuzil.av3.entity.modules.render.PhotonModule;
import com.amuzil.av3.entity.modules.render.SoundModule;
import com.google.common.base.MoreObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;


public class AvatarProjectile extends AvatarEntity implements IAvatarProjectile {
    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity cachedOwner;
    public boolean leftOwner;
    private boolean hasBeenShot;

    private static final EntityDataAccessor<Float> WIDTH = SynchedEntityData.defineId(AvatarProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(AvatarProjectile.class, EntityDataSerializers.FLOAT);

    public AvatarProjectile(EntityType<? extends AvatarProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
        // NOTE: Modules are not synced between client and server unless added to the entity's constructor!
        addRenderModule((IRenderModule) ModuleRegistry.create(PhotonModule.id));
        addModule(ModuleRegistry.create(SoundModule.id));
        addModule(ModuleRegistry.create(TimeoutModule.id));
    }

    public AvatarProjectile(Level pLevel) {
        this(AvatarEntities.AVATAR_PROJECTILE_ENTITY_TYPE.get(), pLevel);
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
    public void shoot(Vec3 location, Vec3 direction, double speed, double inAccuracy) {
        setPos(location);
        Vec3 vec3 = direction.normalize().add(this.random.triangle(0.0D, 0.0172275D * inAccuracy), this.random.triangle(0.0D, 0.0172275D * inAccuracy),
                this.random.triangle(0.0D, 0.0172275D * inAccuracy)).scale(speed);
        this.setDeltaMovement(vec3);
        double d0 = vec3.horizontalDistance();
        this.setXRot(owner().getXRot());
        this.setYRot(owner().getYRot());
    }

    @Override
    public @NotNull ItemStack getItem() {
        return new ItemStack(Blocks.AIR);
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (!this.isRemoved() && this.getId() >= 0) {
            super.remove(reason);
        }
    }

    @Override
    public void init() {
        super.init();
        setBoundingBox(getSize());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HEIGHT, 0.5f);
        builder.define(WIDTH, 0.5f);
    }

    // Unironically the worst piece of game design I have ever seen in my life.
    // WHO LOCKS ENTITY SIZE BEHIND STATIC INSTANCES????
    @Override
    protected AABB makeBoundingBox() {
        return getSize();
    }



    public Entity getEffectSource() {
        return MoreObjects.firstNonNull(this.getOwner(), this);
    }

    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }

        if (this.leftOwner) {
            tag.putBoolean("LeftOwner", true);
        }

        tag.putBoolean("HasBeenShot", this.hasBeenShot);
    }

    protected boolean ownedBy(Entity entity) {
        return entity.getUUID().equals(this.ownerUUID);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
            this.cachedOwner = null;
        }

        this.leftOwner = tag.getBoolean("LeftOwner");
        this.hasBeenShot = tag.getBoolean("HasBeenShot");
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick() {
        if (!this.hasBeenShot) {
            this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
            this.hasBeenShot = true;
        }

        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }

        super.tick();
    }

    private boolean checkLeftOwner() {
        Entity owner = this.getOwner();
        if (owner != null) {
            for (Entity entity1 : this.level().getEntities(
                    this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D),
                    (entity) -> !entity.isSpectator() && entity.isPickable())) {
                if (entity1.getRootVehicle() == owner.getRootVehicle()) {
                    return false;
                }
            }
        }

        return true;
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 pos, Vec3 delta) {
        return getEntityHitResult(this.level(), this, pos, delta,
                this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(2.0D),
                this::canHitEntity, 0.3F);
    }

    @Nullable
    public static EntityHitResult getEntityHitResult(Level level, Entity thisEntity, Vec3 pos, Vec3 delta, AABB thisAABB, Predicate<Entity> canBeHit, float scale) {
        double maxDist = Double.MAX_VALUE;
        Entity entity = null;

        for(Entity otherEntity : level.getEntities(thisEntity, thisAABB, canBeHit)) {
            AABB aabb = otherEntity.getBoundingBox().inflate(scale);
            Optional<Vec3> optional = aabb.clip(pos, delta);
            if (optional.isPresent()) {
                double dist = pos.distanceToSqr(optional.get());
                if (dist < maxDist) {
                    entity = otherEntity;
                    maxDist = dist;
                }
            }
        }

        return entity == null ? null : new EntityHitResult(entity);
    }

    /**
     * Similar to setArrowHeading, it's point to throw entity to a (x, y, z) direction.
     */
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        Vec3 vec3 = (new Vec3(x, y, z)).normalize().add(this.random.triangle(0.0D, 0.0172275D * (double)inaccuracy), this.random.triangle(0.0D, 0.0172275D * (double)inaccuracy), this.random.triangle(0.0D, 0.0172275D * (double)inaccuracy)).scale((double)velocity);
        this.setDeltaMovement(vec3);
        double d0 = vec3.horizontalDistance();
        this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
        this.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
        float f = -Mth.sin(y * ((float)Math.PI / 180F)) * Mth.cos(x * ((float)Math.PI / 180F));
        float f1 = -Mth.sin((x + z) * ((float)Math.PI / 180F));
        float f2 = Mth.cos(y * ((float)Math.PI / 180F)) * Mth.cos(x * ((float)Math.PI / 180F));
        this.shoot((double)f, (double)f1, (double)f2, velocity, inaccuracy);
        Vec3 vec3 = shooter.getDeltaMovement();
        this.setDeltaMovement(this.getDeltaMovement().add(vec3.x, shooter.onGround() ? 0.0D : vec3.y, vec3.z));
    }

    protected void onHit(HitResult result) {
        HitResult.Type hitresult$type = result.getType();
        if (hitresult$type == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult)result);
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, result.getLocation(), GameEvent.Context.of(this, (BlockState)null));
        } else if (hitresult$type == HitResult.Type.BLOCK) {
            BlockHitResult blockhitresult = (BlockHitResult)result;
            this.onHitBlock(blockhitresult);
            BlockPos blockpos = blockhitresult.getBlockPos();
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockpos, GameEvent.Context.of(this, this.level().getBlockState(blockpos)));
        }
        if (!this.level().isClientSide)
            this.level().broadcastEntityEvent(this, (byte)3);
    }

    /**
     * Called when the arrow hits an entity
     */
    protected void onHitEntity(EntityHitResult result) {
    }

    protected void onHitBlock(BlockHitResult result) {
        BlockState blockstate = this.level().getBlockState(result.getBlockPos());
//        blockstate.onProjectileHit(this.level(), blockstate, result, this);
    }

    /**
     * Updates the entity motion clientside, called by packets from the server
     */
    public void lerpMotion(double x, double y, double z) {
        this.setDeltaMovement(x, y, z);
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = Math.sqrt(x * x + z * z);
            this.setXRot((float)(Mth.atan2(y, d0) * (double)(180F / (float)Math.PI)));
            this.setYRot((float)(Mth.atan2(x, z) * (double)(180F / (float)Math.PI)));
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
            this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }

    }

    protected boolean canHitEntity(Entity pTarget) {
        if (!pTarget.canBeHitByProjectile()) {
            return false;
        } else {
            Entity entity = this.getOwner();
            return entity == null || this.leftOwner || !entity.isPassengerOfSameVehicle(pTarget);
        }
    }

    protected void updateRotation() {
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = vec3.horizontalDistance();
        this.setXRot(lerpRotation(this.xRotO, (float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI))));
        this.setYRot(lerpRotation(this.yRotO, (float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI))));
    }

    protected static float lerpRotation(float currentRotation, float targetRotation) {
        while(targetRotation - currentRotation < -180.0F) {
            currentRotation -= 360.0F;
        }

        while(targetRotation - currentRotation >= 180.0F) {
            currentRotation += 360.0F;
        }

        return Mth.lerp(0.2F, currentRotation, targetRotation);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        Entity entity = this.level().getEntity(packet.getData());
        if (entity != null) {
            this.setOwner(entity);
        }
    }
}
