package com.amuzil.av3.events;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.renderer.construct.PhysicsBlockRenderer;
import com.amuzil.av3.renderer.mc.MarchingCubesEntityRenderer;
import com.amuzil.av3.renderer.mobs.SkyBisonRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Avatar.MOD_ID, value = Dist.CLIENT)
public class ClientRegistrationEvents {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {

        event.registerEntityRenderer(AvatarEntities.PROJECTILE_ENTITY_TYPE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(AvatarEntities.DIRECT_PROJECTILE_ENTITY_TYPE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(AvatarEntities.CURVE_PROJECTILE_ENTITY_TYPE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(AvatarEntities.BOUND_PROJECTILE_ENTITY_TYPE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(AvatarEntities.ORBIT_PROJECTILE_ENTITY_TYPE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(AvatarEntities.PHYSICS_CONTROLLER_ENTITY_TYPE.get(), NoopRenderer::new);
        //  Placeholder for now
        event.registerEntityRenderer(AvatarEntities.ELEMENT_COLLIDER_ENTITY_TYPE.get(), ThrownItemRenderer::new);

        event.registerEntityRenderer(AvatarEntities.WATER_PROJECTILE_ENTITY_TYPE.get(), MarchingCubesEntityRenderer::new);
        event.registerEntityRenderer(AvatarEntities.WATER_RING_TYPE.get(), MarchingCubesEntityRenderer::new);
        event.registerEntityRenderer(AvatarEntities.WATER_SHIELD_TYPE.get(), MarchingCubesEntityRenderer::new);

        event.registerEntityRenderer(AvatarEntities.RIGID_BLOCK_ENTITY_TYPE.get(), PhysicsBlockRenderer::new);

        event.registerEntityRenderer(AvatarEntities.SKYBISON_ENTITY_TYPE.get(), SkyBisonRenderer::new);
    }
}
