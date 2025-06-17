package com.amuzil.omegasource.entity;

import com.amuzil.omegasource.entity.modules.IForceModule;
import com.amuzil.omegasource.entity.modules.IRenderModule;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;


public class AvatarBind extends AvatarEntity implements ItemSupplier {

    private static final EntityDataAccessor<Float> WIDTH = SynchedEntityData.defineId(AvatarBind.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(AvatarBind.class, EntityDataSerializers.FLOAT);

    public AvatarBind(Level pLevel) {
        this(AvatarEntities.AVATAR_BIND_ENTITY_TYPE.get(), pLevel);
    }

    public AvatarBind(EntityType<AvatarBind> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        addForceModule((IForceModule) ModuleRegistry.create("Bind"));
        addRenderModule((IRenderModule) ModuleRegistry.create("Photon"));
        addModule(ModuleRegistry.create("Timeout"));
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
