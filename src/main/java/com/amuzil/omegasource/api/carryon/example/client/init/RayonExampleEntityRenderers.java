package com.amuzil.omegasource.api.carryon.example.client.init;

import com.amuzil.omegasource.api.carryon.example.client.entity.renderer.PhysicsFallingBlockRenderer;
import com.amuzil.omegasource.api.carryon.example.init.RayonExampleEntities;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class RayonExampleEntityRenderers {
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RayonExampleEntities.PHYSICS_FALLING_BLOCK.get(), PhysicsFallingBlockRenderer::new);
    }
}
