package com.amuzil.caliber.physics.mixin.common.entity;

import com.amuzil.caliber.api.PlayerPhysicsElement;
import com.amuzil.caliber.physics.bullet.collision.body.PlayerRigidBody;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin implements PlayerPhysicsElement {

    @Unique
    private PlayerRigidBody rigidBody;
    @Unique private boolean onCollider = false;
    @Unique private Vec3 supportVelocity = Vec3.ZERO;

    // Match the Player constructor signature for 1.21.1:
    // Player(Level level, BlockPos pos, GameProfile profile)
    @Inject(method = "<init>", at = @At("TAIL"))
    public void caliber$initRigidBody(Level level, BlockPos pos, float yRot, GameProfile gameProfile, CallbackInfo ci) {
        this.rigidBody = new PlayerRigidBody(this);
    }

    @Override
    public @Nullable PlayerRigidBody getRigidBody() {
        return this.rigidBody;
    }
    @Override public boolean skipVanillaEntityCollisions() { return this.onCollider; }

    @Override
    public void setGroundContact(boolean onCollider, Vec3 supportVelocity) {
        this.onCollider = onCollider;
        this.supportVelocity = supportVelocity == null ? Vec3.ZERO : supportVelocity;
    }

    /** Cancel vanilla travel while standing on a collider; otherwise leave vanilla untouched. */
    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void caliber$conditionallyHandleTravel(Vec3 input, CallbackInfo ci) {
        if (!this.onCollider) return;

        // We're supported by a collider this tick: override vanilla horizontal move, keep collisions.
        Player self = (Player)(Object)this;

        self.fallDistance = 0.0F;

        // Horizontal intent + platform velocity
        Vec3 intended = new Vec3(input.x, 0.0, input.z);
        Vec3 platform = new Vec3(this.supportVelocity.x, 0.0, this.supportVelocity.z);

        Vec3 vel = self.getDeltaMovement();
        Vec3 newVel = new Vec3(intended.x, Math.max(vel.y, 0.0), intended.z).add(platform);

        self.setDeltaMovement(newVel);
        self.move(MoverType.SELF, self.getDeltaMovement());
        self.setDeltaMovement(self.getDeltaMovement().multiply(0.91, 1.0, 0.91));

        ci.cancel();
    }
}
