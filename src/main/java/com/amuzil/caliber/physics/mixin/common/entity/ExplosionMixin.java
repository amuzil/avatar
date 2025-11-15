package com.amuzil.caliber.physics.mixin.common.entity;

import com.amuzil.caliber.api.elements.rigid.EntityRigidPhysicsElement;
import com.amuzil.caliber.api.elements.PhysicsElement;
import com.amuzil.caliber.physics.bullet.math.Convert;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Allows {@link PhysicsElement} objects to be affected by explosions.
 */
@SuppressWarnings("rawtypes")
@Mixin(Explosion.class)
public class ExplosionMixin {
    @Unique
    private Entity entity;

    @Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;ignoreExplosion(Lnet/minecraft/world/level/Explosion;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void caliber$setCurrentEntity_explode(CallbackInfo ci, Set set, int i, float f2, int k1, int l1, int i2, int i1, int j2, int j1, List list, Vec3 vec3, Iterator var12, Entity entity) {
        this.entity = entity;
    }

    @ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    public Vec3 caliber$setVelocityOfRigidBody_explode(Vec3 velocity) {
        if (EntityRigidPhysicsElement.is(this.entity)) {
            var element = EntityRigidPhysicsElement.get(this.entity);
            element.getPhysicsBody().applyCentralImpulse(Convert.toBullet(velocity).multLocal(element.getPhysicsBody().getMass() * 100f));
        }

        return velocity;
    }
}