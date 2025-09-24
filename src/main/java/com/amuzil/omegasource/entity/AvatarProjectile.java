package com.amuzil.omegasource.entity;

import com.amuzil.omegasource.bending.form.BendingForm;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.entity.modules.IForceModule;
import com.amuzil.omegasource.entity.modules.IRenderModule;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import com.google.common.base.MoreObjects;
import com.lowdragmc.photon.client.fx.EntityEffect;
import com.lowdragmc.photon.client.fx.FX;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;

import javax.annotation.Nullable;
import java.util.UUID;

import static com.amuzil.omegasource.Avatar.*;


public class AvatarProjectile extends AvatarEntity implements IAvatarProjectile, TraceableEntity, ItemSupplier {
    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity cachedOwner;
    private boolean leftOwner;
    private boolean hasBeenShot;

    private static final EntityDataAccessor<Float> WIDTH = SynchedEntityData.defineId(AvatarProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(AvatarProjectile.class, EntityDataSerializers.FLOAT);

//    public AvatarProjectile(Level pLevel) {
//        this(AvatarEntities.AVATAR_PROJECTILE_ENTITY_TYPE.get(), pLevel);
//    }

    public AvatarProjectile(EntityType<? extends AvatarProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        addForceModule((IForceModule) ModuleRegistry.create("Move"));
        addRenderModule((IRenderModule) ModuleRegistry.create("Photon"));
//        addCollisionModule((ICollisionModule) ModuleRegistry.create("Knockback"));
        addModule(ModuleRegistry.create("Timeout"));
    }

    public void startEffect(BendingForm form) {
        FX fx = null;
        if (element() != null) {
            if (element().equals(Elements.FIRE)) {
                if (form.name().equals("strike"))
                    fx = fire_bloom_perma;
                if (form.name().equals("block"))
                    fx = blue_fire_perma;
                if (form.name().equals("arc"))
                    fx = null;
                if (form.name().equals("null"))
                    fx = fire_bloom_perma;
                if (form.name().equals("step"))
                    fx = blue_fire_perma;
            } else if (element().equals(Elements.WATER)) {
                if (form.name().equals("strike"))
                    fx = water;
                if (form.name().equals("block"))
                    fx = water;
            }
        }
        if (fx != null) {
            EntityEffect entityEffect = new EntityEffect(fx, this.level(), this);
            entityEffect.start();
        }
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
    public ItemStack getItem() {
        return ElementProjectile.PROJECTILE_ITEM;
    }

    @Override
    public void remove(RemovalReason pReason) {
        super.remove(pReason);
    }

    @Override
    public void init() {
        super.init();
        setBoundingBox(getSize());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HEIGHT, 0.5f);
        this.entityData.define(WIDTH, 0.5f);
    }

    // Unironically the worst piece of game design I have ever seen in my life.
    // WHO LOCKS ENTITY SIZE BEHIND STATIC INSTANCES????
    @Override
    protected AABB makeBoundingBox() {
        return getSize();
    }



    /**
     * Returns null or the entityliving it was ignited by
     */
    @Nullable
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            this.cachedOwner = ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else if (this.owner() != null) {
            this.cachedOwner = this.owner();
            this.ownerUUID = this.cachedOwner.getUUID();
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    public Entity getEffectSource() {
        return MoreObjects.firstNonNull(this.getOwner(), this);
    }

    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }

        if (this.leftOwner) {
            pCompound.putBoolean("LeftOwner", true);
        }

        pCompound.putBoolean("HasBeenShot", this.hasBeenShot);
    }

    protected boolean ownedBy(Entity pEntity) {
        return pEntity.getUUID().equals(this.ownerUUID);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
            this.cachedOwner = null;
        }

        this.leftOwner = pCompound.getBoolean("LeftOwner");
        this.hasBeenShot = pCompound.getBoolean("HasBeenShot");
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
        Entity entity = this.getOwner();
        if (entity != null) {
            for(Entity entity1 : this.level().getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), (p_37272_) -> {
                return !p_37272_.isSpectator() && p_37272_.isPickable();
            })) {
                if (entity1.getRootVehicle() == entity.getRootVehicle()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    public void shoot(double pX, double pY, double pZ, float pVelocity, float pInaccuracy) {
        Vec3 vec3 = (new Vec3(pX, pY, pZ)).normalize().add(this.random.triangle(0.0D, 0.0172275D * (double)pInaccuracy), this.random.triangle(0.0D, 0.0172275D * (double)pInaccuracy), this.random.triangle(0.0D, 0.0172275D * (double)pInaccuracy)).scale((double)pVelocity);
        this.setDeltaMovement(vec3);
        double d0 = vec3.horizontalDistance();
        this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
        this.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    public void shootFromRotation(Entity pShooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        float f = -Mth.sin(pY * ((float)Math.PI / 180F)) * Mth.cos(pX * ((float)Math.PI / 180F));
        float f1 = -Mth.sin((pX + pZ) * ((float)Math.PI / 180F));
        float f2 = Mth.cos(pY * ((float)Math.PI / 180F)) * Mth.cos(pX * ((float)Math.PI / 180F));
        this.shoot((double)f, (double)f1, (double)f2, pVelocity, pInaccuracy);
        Vec3 vec3 = pShooter.getDeltaMovement();
        this.setDeltaMovement(this.getDeltaMovement().add(vec3.x, pShooter.onGround() ? 0.0D : vec3.y, vec3.z));
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    protected void onHit(HitResult pResult) {
        HitResult.Type hitresult$type = pResult.getType();
        if (hitresult$type == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult)pResult);
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, pResult.getLocation(), GameEvent.Context.of(this, (BlockState)null));
        } else if (hitresult$type == HitResult.Type.BLOCK) {
            BlockHitResult blockhitresult = (BlockHitResult)pResult;
            this.onHitBlock(blockhitresult);
            BlockPos blockpos = blockhitresult.getBlockPos();
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockpos, GameEvent.Context.of(this, this.level().getBlockState(blockpos)));
        }

    }

    /**
     * Called when the arrow hits an entity
     */
    protected void onHitEntity(EntityHitResult pResult) {
    }

    protected void onHitBlock(BlockHitResult pResult) {
        BlockState blockstate = this.level().getBlockState(pResult.getBlockPos());
//        blockstate.onProjectileHit(this.level(), blockstate, pResult, this);
    }

    /**
     * Updates the entity motion clientside, called by packets from the server
     */
    public void lerpMotion(double pX, double pY, double pZ) {
        this.setDeltaMovement(pX, pY, pZ);
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = Math.sqrt(pX * pX + pZ * pZ);
            this.setXRot((float)(Mth.atan2(pY, d0) * (double)(180F / (float)Math.PI)));
            this.setYRot((float)(Mth.atan2(pX, pZ) * (double)(180F / (float)Math.PI)));
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

    protected static float lerpRotation(float pCurrentRotation, float pTargetRotation) {
        while(pTargetRotation - pCurrentRotation < -180.0F) {
            pCurrentRotation -= 360.0F;
        }

        while(pTargetRotation - pCurrentRotation >= 180.0F) {
            pCurrentRotation += 360.0F;
        }

        return Mth.lerp(0.2F, pCurrentRotation, pTargetRotation);
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.getId());
    }

    public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);
        Entity entity = this.level().getEntity(pPacket.getData());
        if (entity != null) {
            this.setOwner(entity);
        }

    }

    public boolean mayInteract(Level pLevel, BlockPos pPos) {
        Entity entity = this.getOwner();
        if (entity instanceof Player) {
            return entity.mayInteract(pLevel, pPos);
        } else {
            return entity == null || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(pLevel, entity);
        }
    }
}
