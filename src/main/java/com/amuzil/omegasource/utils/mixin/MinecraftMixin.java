package com.amuzil.omegasource.utils.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Minecraft.class)
public class MinecraftMixin {
    public MinecraftMixin() {
        System.out.println(">>> [Mixin] MinecraftMixin loaded!");
    }

//    @Inject(method = "tick", at = @At("HEAD"))
//    private void onTick(CallbackInfo ci) {
//        System.out.println("[Mixin] MinecraftMixin loaded!");
//    }
}