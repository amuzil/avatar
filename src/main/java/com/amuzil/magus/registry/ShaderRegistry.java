package com.amuzil.magus.registry;

import com.amuzil.av3.Avatar;
import com.amuzil.magus.client.render.ShaderUniforms;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@EventBusSubscriber(value = Dist.CLIENT, modid = Avatar.MOD_ID)
public class ShaderRegistry {

    public static ShaderInstance
            TRIPLANAR_SHADER,
            STYLISED_WATER;

    public static ShaderUniforms
            STYLISED_WATER_UNIFORMS;

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath("av3", "dynamic_mesh/triplanar"), // matches your .json name
                        DefaultVertexFormat.NEW_ENTITY
                ),
                shader -> TRIPLANAR_SHADER = shader);

        event.registerShader(
                new ShaderInstance(event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath("av3", "dynamic_mesh/stylised_water"),
                        DefaultVertexFormat.NEW_ENTITY
                ),
                shader -> {
                    STYLISED_WATER = shader;
                    STYLISED_WATER_UNIFORMS = new ShaderUniforms.StylisedWaterUniforms(shader);
                });

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

    public static RenderType waterRenderType(ResourceLocation tex) {
        return RenderType.create(
                "stylised_water",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.TRIANGLES,
                512,
                true,
                true,
                RenderType.CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(() -> ShaderRegistry.STYLISED_WATER))
                        .setTransparencyState(WATER_TRANSPARENCY)
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .setTextureState(new RenderStateShard.TextureStateShard(tex, false, false))
                        .setCullState(new RenderStateShard.CullStateShard(false))
                        .createCompositeState(true)
        );
    }

    private static final RenderStateShard.TransparencyStateShard WATER_TRANSPARENCY =
            new RenderStateShard.TransparencyStateShard(
                    "water_transparency",
                    () -> {
                        RenderSystem.enableBlend();
                        RenderSystem.blendFuncSeparate(
                                GlStateManager.SourceFactor.SRC_ALPHA,
                                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                GlStateManager.SourceFactor.ONE,
                                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
                        );
                    },
                    () -> {
                        RenderSystem.disableBlend();
                        RenderSystem.defaultBlendFunc();
                    }
            );
}
