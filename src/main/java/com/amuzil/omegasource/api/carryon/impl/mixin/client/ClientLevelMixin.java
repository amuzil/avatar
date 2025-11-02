package com.amuzil.omegasource.api.carryon.impl.mixin.client;

import com.amuzil.omegasource.api.carryon.impl.event.ClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Shadow @Final private Minecraft minecraft;
    
    @Inject(method = "disconnect", at = @At("HEAD"))
    public void rayon$onDisconnect_disconnect(CallbackInfo ci) {
        ClientEventHandler.onDisconnect(this.minecraft, (ClientLevel)(Object)this);
    }
}
