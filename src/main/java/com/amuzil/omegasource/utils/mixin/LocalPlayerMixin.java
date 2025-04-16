package com.amuzil.omegasource.utils.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player {

    public LocalPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile profile) {
        super(level, pos, yRot, profile);
        System.out.println("[Mixin] LocalPlayerMixin instantiated!");
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void onTravel(Vec3 travelVector, CallbackInfo ci) {
        System.out.println("[Mixin] LocalPlayerMixin loaded!");

        // Check if spacebar is pressed and we're on the ground
        if (this.onGround() && Minecraft.getInstance().options.keyJump.isDown()) {
            // Replace with your super jump
            this.setDeltaMovement(this.getDeltaMovement().x, 1.2D, this.getDeltaMovement().z);
            this.hurtMarked = true;

            // Cancel the method so vanilla jump doesn't happen
            ci.cancel();
        }
    }
}
