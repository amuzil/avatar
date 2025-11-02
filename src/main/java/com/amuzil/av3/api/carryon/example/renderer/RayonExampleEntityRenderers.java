package com.amuzil.av3.api.carryon.example.renderer;

import com.amuzil.av3.api.carryon.example.entity.RayonExampleEntities;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class RayonExampleEntityRenderers {
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RayonExampleEntities.PHYSICS_FALLING_BLOCK.get(), PhysicsFallingBlockRenderer::new);
    }
}
