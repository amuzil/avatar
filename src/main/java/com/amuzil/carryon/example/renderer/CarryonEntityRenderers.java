package com.amuzil.carryon.example.renderer;

import com.amuzil.carryon.example.entity.CarryonEntities;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class CarryonEntityRenderers {
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CarryonEntities.PHYSICS_FALLING_BLOCK.get(), PhysicsFallingBlockRenderer::new);
    }
}
