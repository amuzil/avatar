package com.amuzil.omegasource.entity;

import com.amuzil.omegasource.entity.modules.ICollisionModule;
import com.amuzil.omegasource.entity.modules.IForceModule;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AvatarProjectile extends AvatarEntity implements IAvatarProjectile, ItemSupplier {

    public AvatarProjectile(Level pLevel) {
        super(AvatarEntities.AVATAR_PROJECTILE_ENTITY_TYPE.get(), pLevel);
        addForceModule((IForceModule) ModuleRegistry.create("Move"));
//        addCollisionModule((ICollisionModule) ModuleRegistry.create("Knockback"));
        addModule(ModuleRegistry.create("Timeout"));
    }

    public AvatarProjectile(EntityType<AvatarProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        addForceModule((IForceModule) ModuleRegistry.create("Move"));
//        addCollisionModule((ICollisionModule) ModuleRegistry.create("Knockback"));
        addModule(ModuleRegistry.create("Timeout"));
    }

    @Override
    public void shoot(Vec3 location, Vec3 direction, double speed, double inAccuracy) {
        setPos(location);
        Vec3 vec3 = direction.normalize().add(this.random.triangle(0.0D, 0.0172275D * inAccuracy), this.random.triangle(0.0D, 0.0172275D * inAccuracy),
                this.random.triangle(0.0D, 0.0172275D * inAccuracy)).scale(speed);
        this.setDeltaMovement(vec3);
        double d0 = vec3.horizontalDistance();
        this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)));
        this.setXRot((float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    public ItemStack getItem() {
        return ElementProjectile.PROJECTILE_ITEM;
    }
}
