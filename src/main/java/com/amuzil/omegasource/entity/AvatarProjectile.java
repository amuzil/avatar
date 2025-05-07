package com.amuzil.omegasource.entity;

import com.amuzil.omegasource.bending.form.BendingForm;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.entity.modules.IForceModule;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import com.lowdragmc.photon.client.fx.EntityEffect;
import com.lowdragmc.photon.client.fx.FX;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static com.amuzil.omegasource.Avatar.*;


public class AvatarProjectile extends AvatarEntity implements IAvatarProjectile, ItemSupplier {

    private static final EntityDataAccessor<Float> WIDTH = SynchedEntityData.defineId(AvatarProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(AvatarProjectile.class, EntityDataSerializers.FLOAT);

    public AvatarProjectile(Level pLevel) {
        this(AvatarEntities.AVATAR_PROJECTILE_ENTITY_TYPE.get(), pLevel);
    }

    public AvatarProjectile(EntityType<AvatarProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        addForceModule((IForceModule) ModuleRegistry.create("Move"));
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
}
