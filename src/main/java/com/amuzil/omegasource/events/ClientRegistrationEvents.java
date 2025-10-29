package com.amuzil.omegasource.events;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.entity.AvatarEntities;
import com.amuzil.omegasource.entity.renderer.MarchingCubesEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Avatar.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistrationEvents {
    
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        System.out.println("registering entity renderer");
        event.registerEntityRenderer(AvatarEntities.AVATAR_WATER_ENTITY_TYPE.get(), MarchingCubesEntityRenderer::new);
    }
}
