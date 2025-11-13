package com.amuzil.caliber.physics.mixin.common.entity;

import com.amuzil.caliber.api.EntityPhysicsElement;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

/**
 * Prevents certain packets from being sent for {@link EntityPhysicsElement}s.
 */
//TODO: Somehow override the broadcast consumer so we don't have to have a bunch of these methods?
@SuppressWarnings({"rawtypes", "unchecked"})
@Mixin(ServerEntity.class)
public class ServerEntityMixin {
    @Shadow
    @Final
    private Entity entity;

    @Redirect(method = "sendChanges", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 1))
    public void caliber$overrideRotation_sendChanges(Consumer consumer, Object object) {
        if (!EntityPhysicsElement.is(this.entity))
            consumer.accept(object);
    }

    @Redirect(method = "sendChanges", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 2))
    public void caliber$overrideVelocity_sendChanges(Consumer consumer, Object object) {
        if (!EntityPhysicsElement.is(this.entity))
            consumer.accept(object);
    }

    @Redirect(method = "sendChanges", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 3))
    public void caliber$overrideMultiple_sendChanges(Consumer consumer, Object object) {
        if (!EntityPhysicsElement.is(this.entity))
            consumer.accept(object);
    }
}
