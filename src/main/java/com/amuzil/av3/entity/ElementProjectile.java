package com.amuzil.av3.entity;

import com.amuzil.av3.bending.element.Element;
import com.amuzil.av3.bending.form.BendingForm;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

@Deprecated
public abstract class ElementProjectile extends Projectile implements ItemSupplier {
    public static final ItemStack PROJECTILE_ITEM = new ItemStack(Blocks.AIR);
    private static final EntityDataAccessor<Byte> ID_FLAGS = SynchedEntityData.defineId(ElementProjectile.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> PIERCE_LEVEL = SynchedEntityData.defineId(ElementProjectile.class, EntityDataSerializers.BYTE);
    protected boolean leftOwner;
    protected boolean hasBeenShot;
    private int life;
    public int ttk = 40;
    public boolean arcActive = false;
    public boolean hasElement = false;
    public BendingForm form;

    public ElementProjectile(EntityType<? extends ElementProjectile> type, Level level) {
        super(type, level);
    }

    public ElementProjectile(EntityType<? extends ElementProjectile> entityType, double x, double y, double z, Level level, BendingForm form) {
        this(entityType, level);
        this.setPos(x, y, z);
        this.setNoGravity(true);
        this.form = form;
    }

    public ElementProjectile(EntityType<? extends ElementProjectile> entityType, LivingEntity livingEntity, Level level, BendingForm form) {
        this(entityType, livingEntity.getX(), livingEntity.getEyeY(), livingEntity.getZ(), level, form);
        this.setOwner(livingEntity);
    }

    public abstract Element getElement();

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
//            System.out.println("ENTITY NEARBY: " + otherEntity);
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

    public boolean canHitEntity(Entity otherEntity) {
        if (!otherEntity.canBeHitByProjectile()) {
            return false;
        } else {
            Entity entity = this.getOwner();
            if (entity != null) {
                if (otherEntity instanceof ElementProjectile other) {
                    if (other.arcActive)
                        return true;
                }
            }
            return entity == null || this.leftOwner || !entity.isPassengerOfSameVehicle(otherEntity);
        }
    }

    public void setTimeToKill(int ticks) {
        this.ttk = ticks;
    }

    public void tickDespawn() {
        ++life;
        if (life >= ttk) {
//            System.out.println("BYE BYE BBY " + life + " / " + ttk);
            this.discard();
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ID_FLAGS, (byte)0);
        this.entityData.define(PIERCE_LEVEL, (byte)0);
    }

    public boolean checkLeftOwner() {
        Entity owner = this.getOwner();
        if (owner != null) {
            for(Entity entity1 : this.level().getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), (entity) -> {
                return !entity.isSpectator() && entity.isPickable();
            })) {
                if (entity1.getRootVehicle() == owner.getRootVehicle()) {
                    return false;
                }
            }
        }
        return true;
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

    protected void onHitBlock(BlockHitResult blockHitResult) {
//        super.onHitBlock(blockHitResult);
        this.discard();
    }

    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte)3);
        }
    }

//    public void control(float scale, BendingForm form) {
//        this.arcActive = true;
//        if (form == BendingForms.NULL) {
//            this.hasElement = true;
//            this.setTimeToKill(2400);
//        }
//        Entity owner = this.getOwner();
//        assert owner != null;
//        Vec3[] pose = new Vec3[]{owner.position(), owner.getLookAngle()};
//        pose[1] = pose[1].scale((scale)).add((0), (owner.getEyeHeight()), (0));
//        Vec3 newPos = pose[1].add(pose[0]);
//        this.setPos(newPos.x, newPos.y, newPos.z);
//    }

    @Override
    public ItemStack getItem() {
        return PROJECTILE_ITEM;
    }

//    public static ElementProjectile createElementEntity(BendingForm form, Element element, ServerPlayer player, ServerLevel level) {
//        if (element == Elements.AIR) {
//            return new AirProjectile(player, level);
//        } else if (element == Elements.WATER) {
//            return new WaterProjectile(player, level);
//        } else if (element == Elements.EARTH) {
//            return new EarthProjectile(player, level);
//        } else if (element == Elements.FIRE) {
//            return new FireProjectile(player, level);
//        } else {
//            return null;
//        }
//    }

//     Method to start initial visual effect
//    public void startEffect(BendingForm form, LivingEntity player) {
//        this.form = form; // NOTE: Need this to ensure form is set client-side before onHit event
//        FX fx = null;
//        if (getElement().equals(Elements.FIRE)) {
//            if (form.name().equals("strike"))
//                fx = fire_bloom_perma;
//            if (form.name().equals("block"))
//                fx = blue_fire_perma;
//            if (form.name().equals("arc")) {
//                fx = null;
//                arcActive = true;
//            }
//            if (form.name().equals("null")) {
//                fx = fire_bloom_perma;
//                arcActive = true;
//                hasElement = true;
//                this.setTimeToKill(2400);
//            }
//            if (form.name().equals("step"))
//                fx = blue_fire_perma;
//        } else if (getElement().equals(Elements.WATER)) {
//            if (form.name().equals("strike"))
//                fx = water;
//            if (form.name().equals("block"))
//                fx = water;
//            if (form.name().equals("arc")) {
//                fx = water;
//                arcActive = true;
//            }
//            if (form.name().equals("null")) {
//                fx = water;
//                arcActive = true;
//                hasElement = true;
//                this.setTimeToKill(2400);
//            }
//            if (form.name().equals("step"))
//                fx = blue_fire_perma;
//        }
//        if (fx != null) {
//            EntityEffect entityEffect = new EntityEffect(fx, this.level(), this);
//            entityEffect.start();
//        }
//    }
//
//    public static List<Entity> getNearbyEntities(Entity entity, double blocksRadius) {
//        AABB aabb = entity.getBoundingBox().expandTowards(entity.getDeltaMovement()).inflate(blocksRadius);
//        assert Minecraft.getInstance().level != null;
//        return Minecraft.getInstance().level.getEntities(entity, aabb).stream().toList();
//    }
}