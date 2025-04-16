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

    protected LocalPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile profile) {
        super(level, pos, yRot, profile);
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void onAiStep(CallbackInfo ci) {
        // Only apply when on ground and jump key is pressed
        if (this.onGround() && Minecraft.getInstance().options.keyJump.isDown()) {
            // Set upward velocity for a "super jump"
//            Vec3 motion = this.getDeltaMovement();
//            this.setDeltaMovement(motion.x, 1.2D, motion.z);
//            this.hurtMarked = true;

            // Disable default jump behavior
            this.setOnGround(false);
        }
    }
}
