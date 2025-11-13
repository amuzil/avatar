package com.amuzil.caliber.physics.mixin.client;

import com.amuzil.caliber.physics.utils.debug.CollisionObjectDebugger;
import com.jme3.bullet.objects.PhysicsRigidBody;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds an F3 key combination (F3 + R). It toggles renders for all relevant
 * {@link PhysicsRigidBody} objects.
 */
@Mixin(KeyboardHandler.class)
public abstract class KeyboardMixin {
    @Shadow
    protected abstract void debugFeedbackTranslated(String string, Object... objects);

    @Inject(method = "handleDebugKeys", at = @At("HEAD"), cancellable = true)
    private void caliber$processExtraF3_handleDebugKeys(int key, CallbackInfoReturnable<Boolean> info) {
        if (key == 82) { // R key
            boolean enabled = CollisionObjectDebugger.toggle();

            if (enabled)
                this.debugFeedbackTranslated("Rigid Body Hitboxes: shown");
            else
                this.debugFeedbackTranslated("Rigid Body Hitboxes: hidden");

            info.setReturnValue(true);
        }
    }
}
