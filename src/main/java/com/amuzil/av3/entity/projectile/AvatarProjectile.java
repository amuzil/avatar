package com.amuzil.av3.entity.projectile;

import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IAvatarProjectile;
import com.amuzil.av3.entity.api.IFXModule;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;

import com.amuzil.magus.physics.core.ForceCloud;
import com.amuzil.magus.physics.core.ForcePoint;
import com.amuzil.av3.entity.api.modules.client.PhotonModule;
import com.amuzil.av3.entity.api.modules.client.SoundModule;
import com.amuzil.av3.entity.api.modules.entity.TimeoutModule;
import com.google.common.base.MoreObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
    private static final EntityDataAccessor<Float> WIDTH = SynchedEntityData.defineId(AvatarProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(AvatarProjectile.class, EntityDataSerializers.FLOAT);
    public boolean leftOwner;
    public ForceCloud cloud;
    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity cachedOwner;
    private boolean hasBeenShot;

    public AvatarProjectile(EntityType<? extends AvatarProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
        // NOTE: Modules are not synced between client and server unless added to the entity's constructor!
        addClientModule((IFXModule) ModuleRegistry.create(PhotonModule.id));
        addClientModule((IFXModule) ModuleRegistry.create(SoundModule.id));
        addModule(ModuleRegistry.create(TimeoutModule.id));
    }

    public AvatarProjectile(Level pLevel) {
        this(AvatarEntities.AVATAR_PROJECTILE_ENTITY_TYPE.get(), pLevel);
    }

    @Nullable
    public static EntityHitResult getEntityHitResult(Level level, Entity thisEntity, Vec3 pos, Vec3 delta, AABB thisAABB, Predicate<Entity> canBeHit, float scale) {
        double maxDist = Double.MAX_VALUE;
        Entity entity = null;

        for (Entity otherEntity : level.getEntities(thisEntity, thisAABB, canBeHit)) {
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

    protected static float lerpRotation(float pCurrentRotation, float pTargetRotation) {
        while (pTargetRotation - pCurrentRotation < -180.0F) {
            pCurrentRotation -= 360.0F;
        }

        while (pTargetRotation - pCurrentRotation >= 180.0F) {
            pCurrentRotation += 360.0F;
        }

        return Mth.lerp(0.2F, pCurrentRotation, pTargetRotation);
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

        int count = 64;
//        ForceCloud cloud = new ForceCloud(0, count, "projectile_cloud", location, vec3, Vec3.ZERO, getUUID(), null);
//        cloud.setLifetimeSeconds(12.0f);
//
        for (int i = 0; i < count; i++) {
            // scatter a bit around origin
            double rx = (level().random.nextDouble() - 0.5) * 0.75;
            double ry = (level().random.nextDouble() - 0.5) * 0.75;
            double rz = (level().random.nextDouble() - 0.5) * 0.75;

            Vec3 pos = location.add(rx, ry, rz);

            // initial velocity roughly in 'direction'
//                                  .add((float) ((level().random.nextDouble() - 0.5) * 0.1),
//                                        (float) ((level().random.nextDouble() - 0.5) * 0.1),
//                                        (float) ((level().random.nextDouble() - 0.5) * 0.1));

            Vec3 force = Vec3.ZERO; // start with no force, just velocity

//            ForcePoint p = new ForcePoint(0, pos, Vec3.ZERO, force);
//            p.mass(1.0);    // if you have mass setters
//            p.damping(0.1); // mild drag
////                if (level instanceof ServerLevel server && entity instanceof ServerPlayer)
////                   server.sendParticles((ServerPlayer) entity, ParticleTypes.SMOKE, false, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0.1f);
//
//            cloud.addPoints(p);
        }

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
        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
            this.cachedOwner = null;
        }

        this.leftOwner = tag.getBoolean("LeftOwner");
        this.hasBeenShot = tag.getBoolean("HasBeenShot");
    }

    @Override
    public Vec3 getDeltaMovement() {
//        return Vec3.ZERO;
        return super.getDeltaMovement();
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick() {
        super.tick();
        if (!this.hasBeenShot) {
            this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
            this.hasBeenShot = true;


//            MinecraftSpace space = MinecraftSpace.get(level());
            // 4-5 clouds doing flamethrower shit
            // 20 clouds doing flamethrower shit
            // 20 * 200 * 20 = 800,000
            // 20 * 50 * 20  = 200,000

            Vec3 motion = getDeltaMovement().scale(2);

//            if (space != null) {
//                ForceSystem fs = space.forceSystem();
//
//                // type is whatever you use for element (e.g. FIRE = 1, WATER = 2, etc.)
//                int type = 1; // example
//                int maxPoints = 64;
//
//                // create some points
//                int count = 64;
//                Vec3 origin = position();
//
//                Random rand = new Random();
//                Vector3f vel = this.getMotionDirection().step().normalize();
//
//                for (int j = 0; j < 1; j++) {
//                    ForceCloud cloud = fs.createCloud(type, maxPoints, "test", owner() == null ? this : owner(), position(), Vec3.ZERO, Vec3.ZERO);
//                    cloud.setLifetimeSeconds(6.0f);
////
//                    for (int i = 0; i < count; i++) {
//                        // scatter a bit around origin
//                        double rx = (level().random.nextDouble() - 0.5) * 0.75;
//                        double ry = (level().random.nextDouble() - 0.5) * 0.75;
//                        double rz = (level().random.nextDouble() - 0.5) * 0.75;
//
//                        Vec3 pos = origin.add(rx * 2, ry, rz);
//
//                        // initial velocity roughly in 'direction'
////                                  .add((float) ((level().random.nextDouble() - 0.5) * 0.1),
////                                        (float) ((level().random.nextDouble() - 0.5) * 0.1),
////                                        (float) ((level().random.nextDouble() - 0.5) * 0.1));
//
//                        Vec3 force = Vec3.ZERO; // start with no force, just velocity
//
//                        ForcePoint p = new ForcePoint(type, pos, Vec3.ZERO, force);
//                        p.mass(1.0);    // if you have mass setters
//                        p.damping(0.1); // mild drag
////                if (level instanceof ServerLevel server && entity instanceof ServerPlayer)
////                   server.sendParticles((ServerPlayer) entity, ParticleTypes.SMOKE, false, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0.1f);
//
//                        cloud.addPoints(p);
//                    }
//
//                    if (!level().isClientSide) {
//                        space.forceSystem().spawnCloud(cloud, owner() == null ? this : owner());
//                    }
//                }
//            }
        }

//        if (cloud != null) {
//            cloud.tick(1 / 20f);
//            cloud.rebuildSpatialGrid();
//
//            if (cloud.isDead())
//                cloud = null;
//        }

        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }


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

    /**
     * Similar to setArrowHeading, it throws an entity toward a (x, y, z) direction
     */

    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        Vec3 vec3 = (new Vec3(x, y, z)).normalize().add(this.random.triangle(0.0D, 0.0172275D * (double) inaccuracy), this.random.triangle(0.0D, 0.0172275D * (double) inaccuracy), this.random.triangle(0.0D, 0.0172275D * (double) inaccuracy)).scale(velocity);
        this.setDeltaMovement(vec3);
        double d0 = vec3.horizontalDistance();
        this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)));
        this.setXRot((float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();

    }


    public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
        float f = -Mth.sin(y * ((float) Math.PI / 180F)) * Mth.cos(x * ((float) Math.PI / 180F));
        float f1 = -Mth.sin((x + z) * ((float) Math.PI / 180F));
        float f2 = Mth.cos(y * ((float) Math.PI / 180F)) * Mth.cos(x * ((float) Math.PI / 180F));
        this.shoot(f, f1, f2, velocity, inaccuracy);
        Vec3 vec3 = shooter.getDeltaMovement();
        this.setDeltaMovement(this.getDeltaMovement().add(vec3.x, shooter.onGround() ? 0.0D : vec3.y, vec3.z));
    }

    protected void onHit(HitResult result) {
        HitResult.Type hitresult$type = result.getType();
        if (hitresult$type == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult) result);
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, result.getLocation(), GameEvent.Context.of(this, null));
        } else if (hitresult$type == HitResult.Type.BLOCK) {
            BlockHitResult blockhitresult = (BlockHitResult) result;
            this.onHitBlock(blockhitresult);
            BlockPos blockpos = blockhitresult.getBlockPos();
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockpos, GameEvent.Context.of(this, this.level().getBlockState(blockpos)));
        }
        if (!this.level().isClientSide)
            this.level().broadcastEntityEvent(this, (byte) 3);

    }

    @Override
    public void kill() {
        super.kill();
        cloud = null;
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
            this.setXRot((float) (Mth.atan2(y, d0) * (double) (180F / (float) Math.PI)));
            this.setYRot((float) (Mth.atan2(x, z) * (double) (180F / (float) Math.PI)));
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
        this.setXRot(lerpRotation(this.xRotO, (float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI))));
        this.setYRot(lerpRotation(this.yRotO, (float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI))));
    }

//    protected static float lerpRotation(float currentRotation, float targetRotation) {
//        while(targetRotation - currentRotation < -180.0F) {
//            currentRotation -= 360.0F;
//        }
//
//        while(targetRotation - currentRotation >= 180.0F) {
//            currentRotation += 360.0F;
//        }
//
//        return Mth.lerp(0.2F, currentRotation, targetRotation);
//    }
}
