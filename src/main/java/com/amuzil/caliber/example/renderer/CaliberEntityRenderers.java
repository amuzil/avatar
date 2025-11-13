package com.amuzil.caliber.example.renderer;

import com.amuzil.caliber.example.entity.CaliberEntities;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class CaliberEntityRenderers {
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CaliberEntities.PHYSICS_FALLING_BLOCK.get(), PhysicsFallingBlockRenderer::new);
    }
}
