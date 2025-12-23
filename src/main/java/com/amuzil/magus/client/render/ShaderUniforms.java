package com.amuzil.magus.client.render;

import com.lowdragmc.photon.client.gameobject.emitter.data.material.ShaderInstanceMaterial;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;

public class ShaderUniforms {
    public static final class StylisedWaterUniforms {
        // matrices are set by vanilla; you generally don’t touch ModelViewMat/ProjMat manually.

        public final AbstractUniform GameTime;
        public final AbstractUniform Centre;

        public final AbstractUniform WaveScale, WaveSpeed, WaveStrength;
        public final AbstractUniform NoiseScale, NoiseSpeed, NoiseStrength;

        public final AbstractUniform Bands, BandFactor, BandingBias;
        public final AbstractUniform HDRColor, Alpha, ColorIntensity;

        public final AbstractUniform HorizontalFrequency, VerticalFrequency;
        public final AbstractUniform Spin, Size;

        public final AbstractUniform FogStart, FogEnd, FogColor; // if you want to override
        // FogShape is set by engine; usually don’t set it.

        StylisedWaterUniforms(ShaderInstance s) {
            GameTime = s.safeGetUniform("GameTime");
            Centre = s.safeGetUniform("Centre");

            WaveScale = s.safeGetUniform("WaveScale");
            WaveSpeed = s.safeGetUniform("WaveSpeed");
            WaveStrength = s.safeGetUniform("WaveStrength");

            NoiseScale = s.safeGetUniform("NoiseScale");
            NoiseSpeed = s.safeGetUniform("NoiseSpeed");
            NoiseStrength = s.safeGetUniform("NoiseStrength");

            Bands = s.safeGetUniform("Bands");
            BandFactor = s.safeGetUniform("BandFactor");
            BandingBias = s.safeGetUniform("BandingBias");

            HDRColor = s.safeGetUniform("HDRColor");
            Alpha = s.safeGetUniform("Alpha");
            ColorIntensity = s.safeGetUniform("ColorIntensity");

            HorizontalFrequency = s.safeGetUniform("HorizontalFrequency");
            VerticalFrequency = s.safeGetUniform("VerticalFrequency");

            Spin = s.safeGetUniform("Spin");
            Size = s.safeGetUniform("Size");

            FogStart = s.safeGetUniform("FogStart");
            FogEnd = s.safeGetUniform("FogEnd");
            FogColor = s.safeGetUniform("FogColor");
        }
    }
}
