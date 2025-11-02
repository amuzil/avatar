package com.amuzil.av3.entity.projectile;

import com.amuzil.av3.bending.form.BendingForms;
import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.events.FormActivatedEvent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;

@Deprecated
public class FireProjectile extends AvatarProjectile {
    private static final EntityDataAccessor<Byte> ID_FLAGS = SynchedEntityData.defineId(FireProjectile.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> PIERCE_LEVEL = SynchedEntityData.defineId(FireProjectile.class, EntityDataSerializers.BYTE);

    public FireProjectile(EntityType<AvatarProjectile> type, Level level) {
        super(type, level);
        NeoForge.EVENT_BUS.register(this);
    }

//    public FireProjectile(double x, double y, double z, Level level) {
//        this(AvatarEntities.FIRE_PROJECTILE_ENTITY_TYPE.get(), level);
//        this.setPos(x, y, z);
//    }

    public FireProjectile(LivingEntity livingEntity, Level level) {
        this(AvatarEntities.AVATAR_PROJECTILE_ENTITY_TYPE.get(), level);
        this.setPos(livingEntity.getX(), livingEntity.getEyeY(), livingEntity.getZ());
        this.setOwner(livingEntity);
        this.setNoGravity(true);
    }

//    public void tick() {
//        super.tick();
//        boolean flag = this.isNoPhysics();
//        Vec3 deltaMovement = this.getDeltaMovement();
//        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
//            double distance = deltaMovement.horizontalDistance();
//            this.setYRot((float)(Mth.atan2(deltaMovement.x, deltaMovement.z) * (double)(180F / (float)Math.PI)));
//            this.setXRot((float)(Mth.atan2(deltaMovement.y, distance) * (double)(180F / (float)Math.PI)));
//            this.yRotO = this.getYRot();
//            this.xRotO = this.getXRot();
//        }
//
//        BlockPos blockpos = this.blockPosition();
//        BlockState blockstate = this.level().getBlockState(blockpos);
//        if (!blockstate.isAir() && !flag) {
//            VoxelShape voxelshape = blockstate.getCollisionShape(this.level(), blockpos);
//            if (!voxelshape.isEmpty()) {
//                Vec3 vec31 = this.position();
//
//                for(AABB aabb : voxelshape.toAabbs()) {
//                    if (aabb.move(blockpos).contains(vec31)) {
//                        break;
//                    }
//                }
//            }
//        }
//
//        if (this.isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW) || this.isInFluidType((fluidType, height) -> this.canFluidExtinguish(fluidType))) {
//            this.clearFire();
//        }
//
//        Vec3 pos = this.position();
//        Vec3 delta = pos.add(deltaMovement);
//        HitResult hitresult = this.level().clip(new ClipContext(pos, delta, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
//        if (hitresult.getType() != HitResult.Type.MISS) {
//            delta = hitresult.getLocation();
//        }
//
//        while(!this.isRemoved()) {
//            if (!this.level().isClientSide) {
//                this.tickDespawn();
//            }
//            EntityHitResult entityhitresult = this.findHitEntity(pos, delta);
//            if (entityhitresult != null) {
//                hitresult = entityhitresult;
//            }
//
//            if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
//                assert hitresult instanceof EntityHitResult;
//                Entity entity = ((EntityHitResult)hitresult).getEntity();
//                Entity owner = this.getOwner();
//                if (entity instanceof Player && owner instanceof Player && !((Player)owner).canHarmPlayer((Player)entity)) {
//                    hitresult = null;
//                    entityhitresult = null;
//                }
//            }
//
//            if (hitresult != null && hitresult.getType() != HitResult.Type.MISS && !flag) {
////                if (net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult))
////                    break;
//                this.onHit(hitresult);
//                this.hasImpulse = true;
//                break;
//            }
//
//            if (entityhitresult == null) {
//                break;
//            }
//
//            hitresult = null;
//        }
//        deltaMovement = this.getDeltaMovement();
//        double x = deltaMovement.x;
//        double y = deltaMovement.y;
//        double z = deltaMovement.z;
//
//        double finalX = this.getX() + x;
//        double finalY = this.getY() + y;
//        double finalZ = this.getZ() + z;
//        double d4 = deltaMovement.horizontalDistance();
//        if (flag) {
//            this.setYRot((float)(Mth.atan2(-x, -z) * (double)(180F / (float)Math.PI)));
//        } else {
//            this.setYRot((float)(Mth.atan2(x, z) * (double)(180F / (float)Math.PI)));
//        }
//
//        this.setXRot((float)(Mth.atan2(y, d4) * (double)(180F / (float)Math.PI)));
//        this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
//        this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
//
//        float f = 0.49F; // Scale speed
//        this.setDeltaMovement(deltaMovement.scale(f));
//        if (!this.isNoGravity() && !flag) { // Apply gravity
//            Vec3 vec34 = this.getDeltaMovement();
//            this.setDeltaMovement(vec34.x, vec34.y - (double)0.05F, vec34.z);
//        }
//
//        Entity owner = this.getOwner();
//        if (owner != null) {
//            Vec3 vec34 = this.getDeltaMovement();
//            double rateOfControl = 0.4; // Control/curve the shot projectile
//            Vec3 aim = this.getOwner().getLookAngle().multiply(rateOfControl, rateOfControl, rateOfControl);
//            this.setDeltaMovement(vec34.add(aim));
//        }
//        this.setPos(finalX, finalY, finalZ);
//
//        this.checkInsideBlocks();
//    }

    @SubscribeEvent
    public void onFormEvent(FormActivatedEvent event) {
        Entity owner = this.getOwner();
        if (owner != null && event.getEntity().getId() == owner.getId()) {
            if (event.getActiveForm().equals(BendingForms.STRIKE)) {
                // If LivingEntity owns this entity and has activated strike form, shoot held element
//                this.arcActive = false;
//                this.setTimeToKill(100);
                if (!this.level().isClientSide()) {
                    this.shoot(owner.getViewVector(1).x, owner.getViewVector(1).y, owner.getViewVector(1).z, 0.75F, 1);
                    this.discard();
                }
            }
        }
    }

//    @Nullable
//    protected EntityHitResult findHitEntity(Vec3 pos, Vec3 delta) {
//        return getEntityHitResult(this.level(), this, pos, delta,
//                this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(2.0D),
//                this::canHitEntity, 0.3F);
//    }

//    @Nullable
//    public static EntityHitResult getEntityHitResult(Level level, Entity thisEntity, Vec3 pos, Vec3 delta, AABB thisAABB, Predicate<Entity> canBeHit, float scale) {
//        double maxDist = Double.MAX_VALUE;
//        Entity entity = null;
//
//        for(Entity otherEntity : level.getEntities(thisEntity, thisAABB, canBeHit)) {
////            System.out.println("ENTITY NEARBY: " + otherEntity);
//            AABB aabb = otherEntity.getBoundingBox().inflate(scale);
//            Optional<Vec3> optional = aabb.clip(pos, delta);
//            if (optional.isPresent()) {
//                double dist = pos.distanceToSqr(optional.get());
//                if (dist < maxDist) {
//                    entity = otherEntity;
//                    maxDist = dist;
//                }
//            }
//        }
//
//        return entity == null ? null : new EntityHitResult(entity);
//    }

//    public boolean canHitEntity(Entity otherEntity) {
//        if (!otherEntity.canBeHitByProjectile()) {
//            return false;
//        } else {
//            Entity entity = this.getOwner();
////            if (entity != null) {
////                if (otherEntity instanceof TestProjectileEntity other) {
////                    System.out.println("THIS OWNER: " + entity + " | " + !entity.isPassengerOfSameVehicle(otherEntity));
////                    System.out.println("THAT OWNER: " + other.getOwner());
////                }
////            }
//            return entity == null || this.leftOwner || !entity.isPassengerOfSameVehicle(otherEntity);
//        }
//    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_FLAGS, (byte)0);
        this.entityData.define(PIERCE_LEVEL, (byte)0);
    }

    public boolean isNoPhysics() {
        if (!this.level().isClientSide) {
            return this.noPhysics;
        } else {
            return (this.entityData.get(ID_FLAGS) & 2) != 0;
        }
    }

//    public void handleEntityEvent(byte data) {
//        if (data == 3) {
//            System.out.println("HANDLE ENTITY EVENT");
//            for(int i = 0; i < 8; ++i) {
//                this.level().addParticle(this.getParticle(), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
//            }
//        }
//    }

//    protected void onHitEntity(EntityHitResult entityHitResult) {
//        Entity entity = entityHitResult.getEntity();
//        if (entity instanceof Blaze) {
//            if (this.getOwner() != null) {
//                this.setNoGravity(false);
//                this.setOwner(entity);
//                this.shoot(entity.position().add(0, entity.getEyeHeight(), 0), entity.getLookAngle(), 0.75F, 0);
//                this.leftOwner = true;
//                System.out.println("Hit blaze, deflect!!!");
//            }
//        }
//        else if (entity instanceof FireProjectile fireProjectile) {
//            if (this.getOwner() != null && this.level().isClientSide) {
//                if (fireProjectile.arcActive && !fireProjectile.hasElement && this.checkLeftOwner()) {
//                    this.setOwner(elementProjectile.getOwner()); // Give control to receiver
//                    this.setDeltaMovement(0,0,0); // Full stop
//                    this.arcActive = true; // Enable control of this shot projectile
//                    this.discard();
//                    fireProjectile.hasElement = true;
//
//                    AvatarNetwork.sendToServer(new ActivatedFormPacket(fireProjectile.getId()));
//                } else {
//                    if (!this.getOwner().equals(fireProjectile.getOwner())) {
//                        ElementCollision collisionEntity = new ElementCollision(this.getX(), this.getY(), this.getZ(), this.level());
//                        collisionEntity.setTimeToKill(5);
//                        this.level().addFreshEntity(collisionEntity);
//                        EntityEffect entityEffect = new EntityEffect(orb_bloom, this.level(), collisionEntity);
//                        entityEffect.start();
//                        this.discard();
//                        fireProjectile.discard();
//                    }
//                }
//            }
//        }
//        else if (entity instanceof WaterProjectile waterProjectile) {
//            if (this.getOwner() != null && this.level().isClientSide) {
//                if (!this.getOwner().equals(waterProjectile.getOwner())) {
//                    ElementCollision collisionEntity = new ElementCollision(this.getX(), this.getY(), this.getZ(), this.level());
//                    collisionEntity.setTimeToKill(5);
//                    this.level().addFreshEntity(collisionEntity);
//                    EntityEffect entityEffect = new EntityEffect(steam, this.level(), collisionEntity);
//                    entityEffect.start();
//                    this.discard();
//                    waterProjectile.discard();
//                }
//            }
//        }
//        else if (entity instanceof Fireball fireBall) {
//            if (!this.getOwner().equals(fireBall.getOwner())) {
//                fireBall.discard();
//            }
//        } else if (entity instanceof AbstractArrow arrow) {
//            if (!this.getOwner().equals(arrow.getOwner())) {
//                arrow.discard();
//            }
//        } else {
//            float i = 10; // Deal 10 damage
//            entity.hurt(this.damageSources().thrown(this, this.getOwner()), i);
//            this.discard();
//        }
//    }

    protected void onHitBlock(BlockHitResult blockHitResult) {
//        super.onHitBlock(blockHitResult);
        this.discard();
    }

    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide)
            this.level().broadcastEntityEvent(this, (byte)3);
    }
}