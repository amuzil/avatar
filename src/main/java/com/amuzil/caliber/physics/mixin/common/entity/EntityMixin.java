package com.amuzil.caliber.physics.mixin.common.entity;

import com.amuzil.caliber.api.EntityPhysicsElement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Basic changes for {@link EntityPhysicsElement}s.
 * ({@link CallbackInfo#cancel()} go brrr)
 */
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    private EntityDimensions dimensions;
    @Shadow
    private Vec3 position;

    @Shadow public abstract EntityType<?> getType();
    @Shadow public abstract Level level();
    @Shadow public abstract Vec3 getDeltaMovement();
    @Shadow public abstract double getY();
    @Shadow public abstract Vec3 position();
    @Shadow public abstract float getBbHeight();
    @Shadow public abstract AABB getBoundingBox();
    @Shadow public abstract boolean isAlive();
    @Shadow public abstract boolean isShiftKeyDown();

    // === Collidability override (Fabric: isCollidable) ===
    @Inject(method = "canBeCollidedWith", cancellable = true, at = @At("RETURN"))
    private void sm$collisionOverride(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;

        Level level = self.level();
        if (!cir.getReturnValue()) {
//            if (self instanceof LivingEntity) {
                boolean returnValue;

                // Equivalent of !getType().isSaveable() from Fabric:
                // in Mojmap, canSerialize()==false means "non-saving" type
                if (!getType().canSerialize()
                        && getType() != EntityType.PLAYER) {
                    returnValue = false;
                } else {
                    returnValue = isAlive();
                }

                cir.setReturnValue(returnValue);
//            }
        }
    }

    // === Entity vs entity collision (Fabric: collidesWith) ===
    @Inject(method = "canCollideWith", cancellable = true, at = @At("RETURN"))
    private void sm$collisionOverride(Entity other, CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        Level level = self.level();

        boolean collides = cir.getReturnValue();
        EntityType<?> thisType = getType();

        if (self instanceof Player && other instanceof Player) {
            // Only affect player-on-player collisions
            collides = false;
        }

        if (collides) {
            if (self.isShiftKeyDown()) {
                if (!level.isClientSide()) {
//                    SolidMobsMain.registerCollisionOnServer(thisType.toString(), other.getType().toString(), false);
                }
                cir.setReturnValue(false);
            } else {
                // Standing-on-top platform check:
                // self is platform, other is possible rider
                collides = self.getY() + 0.01 >= other.getY() + other.getBoundingBox().getYsize();
                if (!level.isClientSide()) {
//                    SolidMobsMain.registerCollisionOnServer(thisType.toString(), other.getType().toString(), collides);
                }
                cir.setReturnValue(collides);
            }
        } else {
            if (!level.isClientSide()) {
//                SolidMobsMain.registerCollisionOnServer(thisType.toString(), other.getType().toString(), collides);
            }
            cir.setReturnValue(collides);
        }
    }

    // === Move entities standing on top of this one (Fabric: tick tail injection) ===
    @Inject(method = "tick", at = @At("TAIL"))
    private void sm$moveWalkingRider(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        Level level = self.level();

//        if (!(self instanceof LivingEntity)) {
//            return;
//        }

        // Non-bouncy slimes etc.
        if ((!getType().equals(EntityType.SLIME) && !getType().equals(EntityType.MAGMA_CUBE))) {

            try {
                AABB expanded = self.getBoundingBox().inflate(-0.03, 0.1, -0.03);
                List<Entity> colliders = level.getEntities(self, expanded, e -> true);

                if (!colliders.isEmpty()) {
                    for (Entity possibleStandingMob : colliders) {
                        if (
                                possibleStandingMob instanceof LivingEntity
                                && possibleStandingMob.getY() >= self.getY() + self.getBbHeight() - 0.06) {

                            if (possibleStandingMob.isShiftKeyDown()) {
//                                if (!solidMobsConfigData.platformMode) {
                                    double modifyY;
                                    if (getType().equals(EntityType.GHAST)) {
                                        modifyY = 0.08;
                                    } else {
                                        modifyY = 1.0 / 16.0;
                                    }

                                    // Snap to centre of mob when sneaking
                                    possibleStandingMob.setPos(
                                            self.position().x(),
                                            self.position().y() + self.getBbHeight() + modifyY,
                                            self.position().z()
                                    );
//                                }
                            } else {
                                Vec3 vel = self.getDeltaMovement().scale(0.833333333333);
                                possibleStandingMob.setDeltaMovement(
                                        possibleStandingMob.getDeltaMovement().add(vel)
                                );
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
                // swallow just like original
            }
        }
    }

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    public void caliber$pushAwayFrom_push(Entity entity, CallbackInfo info) {
        if (EntityPhysicsElement.is((Entity) (Object) this) && EntityPhysicsElement.is(entity))
            info.cancel();
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void caliber$overrideMovement_move(CallbackInfo info) {
        if (EntityPhysicsElement.is((Entity) (Object) this))
            info.cancel();
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    public void caliber$saveRigidBody_saveWithoutId(CompoundTag tag, CallbackInfoReturnable<CompoundTag> info) {
        if (EntityPhysicsElement.is((Entity) (Object) this))
            tag.put("RigidBody", EntityPhysicsElement.get((Entity) (Object) this).getRigidBody().writeTagInfo());
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    public void caliber$readRigidBody_load(CompoundTag tag, CallbackInfo info) {
        if (EntityPhysicsElement.is((Entity) (Object) this))
            EntityPhysicsElement.get((Entity) (Object) this).getRigidBody().readTagInfo(tag.getCompound("RigidBody"));
    }

    @Inject(method = "makeBoundingBox", at = @At("HEAD"), cancellable = true)
    public void caliber$centerBoundingBox_makeBoundingBox(CallbackInfoReturnable<AABB> ci) {
        if (EntityPhysicsElement.is((Entity)(Object)this))
            ci.setReturnValue(this.dimensions.makeBoundingBox(this.position.subtract(0.0D, this.dimensions.height() / 2.0F, 0.0D)));
    }
}
