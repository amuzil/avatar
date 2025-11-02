package com.amuzil.omegasource.events;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.entity.AvatarEntities;
import com.amuzil.omegasource.entity.renderer.MarchingCubesEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Avatar.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistrationEvents {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(AvatarEntities.AVATAR_PROJECTILE_ENTITY_TYPE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(AvatarEntities.AVATAR_DIRECT_PROJECTILE_ENTITY_TYPE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(AvatarEntities.AVATAR_CURVE_PROJECTILE_ENTITY_TYPE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(AvatarEntities.AVATAR_BOUND_PROJECTILE_ENTITY_TYPE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(AvatarEntities.AVATAR_ORBIT_PROJECTILE_ENTITY_TYPE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(AvatarEntities.AVATAR_WATER_PROJECTILE_ENTITY_TYPE.get(), MarchingCubesEntityRenderer::new);
    }
}
