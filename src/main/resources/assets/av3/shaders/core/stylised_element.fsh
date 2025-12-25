#version 330 core

#moj_import <fog.glsl>
#moj_import <photon:particle_utils.glsl>

uniform sampler2D SamplerGradient;
uniform sampler2D NoiseTex;


uniform float GameTime;

uniform float FogStart;
uniform float FogEnd;
uniform vec4  FogColor;
uniform int FogShape;

uniform vec4 ColorModulator;
uniform vec4 HDRColor;
uniform float Alpha;
uniform float Bands;
uniform float BandFactor;

uniform float NoiseScale;
uniform float NoiseStrength;
uniform float NoiseSpeed;

uniform float TimeSpeed;
//time specifically for the wave noise texture
uniform float surface_speed = 1.0;
uniform float Spin; //Twisting motion of the water
uniform float brightness = 0.6;
uniform float ColorIntensity;
//Tiling frequency of the noise accross the mesh
uniform float HorizontalFrequency;
uniform float VerticalFrequency;
//overall size muliplier
uniform float Size;
//affects total size
uniform float BandingBias;

uniform float RimPower;
uniform float EdgeWidth;
uniform float EdgeDarken;

uniform vec4 color1  = vec4(1.0, 1.0, 1.0, 0.5);
uniform vec4 color2  = vec4(0.274, 0.474, 0.98, 0.5);
uniform vec4 color3 = vec4(0.059, 0.389, 0.85, 0.5);
uniform vec4 color4  = vec4(0.0, 0.267, 1.0, 0.5);

in float vertexDistance;
in vec2 texCoord0;
in vec3 LightDir;
in vec4 vertexColor;
in vec3 ViewDir;
in vec3 ViewNormal;
in vec3 ViewPos;

out vec4 fragColor;

void main() {
    float time = GameTime * NoiseSpeed * TimeSpeed;
    float normal_facing = 0.5 * dot(normalize(ViewNormal), normalize(ViewDir)) + 0.5;
    float noise_value = texture(NoiseTex, vec2(texCoord0.x * HorizontalFrequency + Spin * (time / 2.0),
    (texCoord0.y * VerticalFrequency) + time)).r;

    normal_facing += (noise_value - 0.5 + Size) * BandFactor;

    float bands = max(Bands, 1.0);
    float band = normal_facing * bands * BandingBias;

    float t = (band - bands / 2) / max(bands - bands / 2, 1e-6);
//    t = clamp(t, 0.0, 1.0);

    float steps = max(bands, 1.0);

    vec4 band_color = vec4(0,0,0,0);
    if (steps <= 1.0) {
        band_color = getGradientValue(SamplerGradient, 0, 0.5);
    } else {
        // 4) quantize + blend between adjacent steps
        float q  = t * (steps - 1.0);
        float i0 = floor(q);
        float f  = fract(q);

        // gentle blending (less harsh than straight mix)
        f = smoothstep(0.0, 1.0, f);

        float x0 = i0 / (steps - 1.0);
        float x1 = min(i0 + 1.0, steps - 1.0) / (steps - 1.0);

        vec4 c0 = getGradientValue(SamplerGradient, 0, x0);
        vec4 c1 = getGradientValue(SamplerGradient, 0, x1);
        band_color = mix(c0, c1, f);
    }
    // base color (no clamping to brightness)
    vec3 color = brightness * (vec3(1.0) - (band_color.xyz * -ColorIntensity)) * band_color.xyz;

    // include photon particle color pipeline (recommended)
    color *= vertexColor.rgb;
    float rim = pow(1.0 - abs(dot(normalize(ViewNormal), normalize(ViewDir))), RimPower);
    vec3 rimColor = getGradientValue(SamplerGradient, 0, 0.0).rgb;

//    color = mix(color, rimColor, clamp(rim * EdgeWidth, 0.0, 1.0));

    color *= HDRColor.a * HDRColor.rgb;


    // alpha should come from something real
    float a = Alpha * band_color.a * vertexColor.a;
//    a = max(a, rim * 0.15 * Alpha);
    fragColor = linear_fog(vec4(color, a) * ColorModulator, vertexDistance, FogStart, FogEnd, FogColor);
}