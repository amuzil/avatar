package com.amuzil.caliber.physics.mixin.common.entity;

import com.amuzil.caliber.api.elements.rigid.EntityRigidPhysicsElement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Basic changes for {@link EntityRigidPhysicsElement}s.
 * ({@link CallbackInfo#cancel()} go brrr)
 */
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    private EntityDimensions dimensions;
    @Shadow
    private Vec3 position;

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    public void caliber$pushAwayFrom_push(Entity entity, CallbackInfo info) {
        if (EntityRigidPhysicsElement.is((Entity) (Object) this) && EntityRigidPhysicsElement.is(entity))
            info.cancel();
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void caliber$overrideMovement_move(CallbackInfo info) {
        if (EntityRigidPhysicsElement.is((Entity) (Object) this))
            info.cancel();
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    public void caliber$saveRigidBody_saveWithoutId(CompoundTag tag, CallbackInfoReturnable<CompoundTag> info) {
        if (EntityRigidPhysicsElement.is((Entity) (Object) this))
            tag.put("RigidBody", EntityRigidPhysicsElement.get((Entity) (Object) this).getPhysicsBody().writeTagInfo());
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    public void caliber$readRigidBody_load(CompoundTag tag, CallbackInfo info) {
        if (EntityRigidPhysicsElement.is((Entity) (Object) this))
            EntityRigidPhysicsElement.get((Entity) (Object) this).getPhysicsBody().readTagInfo(tag.getCompound("RigidBody"));
    }

    @Inject(method = "makeBoundingBox", at = @At("HEAD"), cancellable = true)
    public void caliber$centerBoundingBox_makeBoundingBox(CallbackInfoReturnable<AABB> ci) {
        if (EntityRigidPhysicsElement.is((Entity)(Object)this))
            ci.setReturnValue(this.dimensions.makeBoundingBox(this.position.subtract(0.0D, this.dimensions.height() / 2.0F, 0.0D)));
    }
}
