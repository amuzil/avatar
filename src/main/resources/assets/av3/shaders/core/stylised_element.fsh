#version 150

#moj_import <fog.glsl>
#moj_import <photon:particle_utils.glsl>

uniform sampler2D SamplerGradient; // Photon gradient sampler
uniform sampler2D NoiseTex;        // Your tiling noise

// Vanilla / extended built-ins
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GameTime;

// Custom uniforms (exposed in Photon inspector)
uniform float DiscardThreshold; // alpha cutoff
uniform float Bands;            // number of toon bands (e.g. 4)
uniform float NoiseScale;       // tiling of noise in UV space
uniform float NoiseAmp;         // how hard noise wobbles bands
uniform float FacingBias;       // bias for fresnel-ish look
uniform float Opacity;          // global alpha multiplier
uniform float TimeScale;        // multiply GameTime (e.g. 20.0)

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;
in vec3 vPosVS;
in vec3 vNormalVS;

out vec4 fragColor;

void main() {
    float bands = max(Bands, 1.0);

    // View dir in view-space (camera at origin)
    vec3 V = normalize(-vPosVS);
    vec3 N = normalize(vNormalVS);

    float facing = clamp(dot(N, V), 0.0, 1.0);

    // Scroll noise along trail/particle UV
    float t = GameTime * TimeScale;
    vec2 nUV = texCoord0 * NoiseScale + vec2(t, -t * 0.7);
    float n = texture(NoiseTex, nUV).r * 2.0 - 1.0;

    // Wobble + bias the facing term
    float f = clamp(facing + n * NoiseAmp + FacingBias, 0.0, 1.0);

    // Quantize into bands (0..1)
    float bandT;
    if (bands <= 1.0) {
        bandT = f;
    } else {
        bandT = floor(f * bands) / (bands - 1.0);
    }

    // Gradient controls palette (air/fire/water are just different gradients)
    vec4 gradColor = getGradientValue(SamplerGradient, 0, bandT);

    vec4 color = gradColor * vertexColor * ColorModulator;
    color.a *= Opacity;

    if (color.a < DiscardThreshold) {
        discard;
    }

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}