package com.amuzil.av3.entity.construct;

import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.caliber.api.EntityPhysicsElement;
import com.amuzil.caliber.physics.bullet.collision.body.EntityRigidBody;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class PhysicsBenderEntity extends AvatarEntity implements EntityPhysicsElement, ItemSupplier, TraceableEntity {
    private final EntityRigidBody rigidBody;

    public PhysicsBenderEntity(EntityType<? extends PhysicsBenderEntity> entityType, Level level) {
        super(entityType, level);
        this.rigidBody = new EntityRigidBody(this);
        this.rigidBody.setKinematic(true);
    }

    @Override
    public @Nullable EntityRigidBody getRigidBody() {
        return rigidBody;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    public @NotNull ItemStack getItem() {
        return new ItemStack(Blocks.AIR);
    }
}
