package com.amuzil.magus.registry;

import com.amuzil.av3.Avatar;
import com.amuzil.magus.client.render.ShaderUniforms;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.Minecraft;
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

    public static VertexFormat
            STYLISED_ELEMENT = VertexFormat.builder().add("Position", VertexFormatElement.POSITION)
            .add("UV0", VertexFormatElement.UV0).add("UV2", VertexFormatElement.UV2)
            .add("Color", VertexFormatElement.COLOR).add("Normal", VertexFormatElement.NORMAL).padding(1).build();

    private static final ResourceLocation
            WHITE_TEX = Avatar.id("textures/water.png"),
            WATER_GRADIENT = Avatar.id("textures/vfx/water_gradient.png"),
            WATER_WAVE_NOISE = Avatar.id("textures/vfx/wave_noise.png");

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
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath("av3", "dynamic_mesh/stylised_water"),
                        STYLISED_ELEMENT
                ),
                shader -> {
                    STYLISED_WATER = shader;
                    STYLISED_WATER_UNIFORMS = new ShaderUniforms.StylisedWaterUniforms(shader);
                    // Values copied from the test effect in Photon
                    ShaderUniforms.StylisedWaterUniforms shaderUniforms = (ShaderUniforms.StylisedWaterUniforms) STYLISED_WATER_UNIFORMS;
                    shaderUniforms.TimeSpeed.set(-150f); // or whatever

                    shaderUniforms.WaveScale.set(0.2f);
                    shaderUniforms.WaveSpeed.set(0.5f);
                    shaderUniforms.WaveStrength.set(0.4f);

                    shaderUniforms.NoiseScale.set(2f);
                    shaderUniforms.NoiseSpeed.set(1.45f);
                    shaderUniforms.NoiseStrength.set(0.08f);

                    shaderUniforms.Bands.set(4.0f);
                    shaderUniforms.BandFactor.set(0.6f);
                    shaderUniforms.BandingBias.set(0.4f);

                    shaderUniforms.Alpha.set(0.6f);
                    shaderUniforms.HDRColor.set(1.0f, 1.0f, 1.0f, 1.0f);
                    shaderUniforms.ColorIntensity.set(1f);

                    shaderUniforms.HorizontalFrequency.set(6.0f);
                    shaderUniforms.VerticalFrequency.set(1.0f);
                    shaderUniforms.Spin.set(3.0f);
                    shaderUniforms.Size.set(2.1f);

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

    public static final RenderStateShard.TexturingStateShard WATER_SETUP =
            new RenderStateShard.TexturingStateShard(
                    "water_setup",
                    () -> {
                        // These run RIGHT before the RenderType draws.
                        RenderSystem.setShaderTexture(7, WATER_GRADIENT);
                        RenderSystem.setShaderTexture(8, WATER_WAVE_NOISE);
                        RenderSystem.setShaderTexture(9, WATER_WAVE_NOISE);

                        ShaderInstance s = RenderSystem.getShader();
                        if (s != null && s.getName().equals(STYLISED_WATER.getName())) {
                            s.setSampler("SamplerGradient", 7);
                            s.setSampler("WaveTex", 8);
                            s.setSampler("NoiseTex", 9);

//                            ((ShaderUniforms.StylisedWaterUniforms) STYLISED_WATER_UNIFORMS).ColorIntensity.set(1f); // could be dynamic
                            float t = (float)(Minecraft.getInstance().level.getGameTime());
                            s.safeGetUniform("GameTime").set(t);
                        }
                    },
                    () -> {
                        // Optional cleanup
                    }
            );

    public static RenderType waterRenderType(ResourceLocation tex) {
        return RenderType.create(
                "stylised_water",
                STYLISED_ELEMENT,
                VertexFormat.Mode.TRIANGLES,
                512,
                true,
                true,
                RenderType.CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(() -> ShaderRegistry.STYLISED_WATER))
                        .setTransparencyState(WATER_TRANSPARENCY)
                        .setTextureState(new RenderStateShard.TextureStateShard(tex, false, false))
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .setTexturingState(WATER_SETUP)
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
