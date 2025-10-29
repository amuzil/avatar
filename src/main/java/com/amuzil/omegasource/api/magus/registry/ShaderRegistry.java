package com.amuzil.omegasource.api.magus.registry;

import com.amuzil.omegasource.Avatar;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = Avatar.MOD_ID)
public class ShaderRegistry {

    public static ShaderInstance TRIPLANAR_SHADER;

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        System.out.println("registering shaders for av3");
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath("av3", "triplanar"), // matches your .json name
                        DefaultVertexFormat.NEW_ENTITY
                ),
                shader -> TRIPLANAR_SHADER = shader
        );
    }

    public static RenderType getTriplanarRenderType(ResourceLocation tex) {
        return RenderType.create(
                "triplanar",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.TRIANGLES,
                256,
                true,
                false,
                RenderType.CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(() -> TRIPLANAR_SHADER))
                        .setTextureState(new RenderStateShard.TextureStateShard(tex, false, false))
                        .setTransparencyState(TRIPLANAR_TRANSPARENCY)
                        .setCullState(new RenderStateShard.CullStateShard(false))
//                        .setDepthTestState(new RenderStateShard.DepthTestStateShard())
                        .createCompositeState(true)
        );
    }

    // replicate vanilla translucent blending (since protected):
    private static final RenderStateShard.TransparencyStateShard TRIPLANAR_TRANSPARENCY =
            new RenderStateShard.TransparencyStateShard(
                    "triplanar_transparency",
                    () -> {
                        RenderSystem.enableBlend();
                        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    },
                    () -> {
                        RenderSystem.disableBlend();
                        RenderSystem.defaultBlendFunc();
                    }
            );
}
