package com.amuzil.caliber.physics.mixin.common.entity;

import com.amuzil.caliber.api.EntityPhysicsElement;
import com.amuzil.caliber.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.PlayerRigidBody;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin implements EntityPhysicsElement {

    @Unique
    private PlayerRigidBody rigidBody;

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
}
