#version 150

#moj_import <fog.glsl>

uniform sampler2D Gradient;
uniform sampler2D NoiseTex;

uniform float GameTime;
uniform float Bands;
uniform float HDR;

uniform float FogStart;
uniform float FogEnd;
uniform vec4  FogColor;

uniform vec4 ColorModulator;
uniform float RimPower;
uniform float EdgeWidth;
uniform float EdgeDarken;

uniform float NoiseScale;
uniform float NoiseStrength;
uniform float NoiseSpeed;

in vec3 vNormal;
in vec3 vViewDir;
in float vDepth;
in float vertexDistance;

out vec4 fragColor;

// fetch 1D gradient
vec3 sampleGradient(float idx) {
    return texture(Gradient, vec2(idx, 0.5)).rgb;
}

void main() {
    float t = GameTime * NoiseSpeed;

    // base lambert + noise
    float n = max(dot(normalize(vNormal), normalize(vViewDir)), 0.0);
    vec2 uvn = gl_FragCoord.xy * NoiseScale;
    float noise = (texture(NoiseTex, uvn + t).r - 0.5) * NoiseStrength;
    n = clamp(n + noise, 0.0, 1.0);

    // toon band index
    float band = floor(n * Bands);

    // gradient sampling assumes evenly spaced bands
    float gidx = band / max(Bands - 1.0, 1.0);
    vec3 color = sampleGradient(gidx);

    // rim highlight
    float rim = pow(1.0 - dot(normalize(vViewDir), normalize(vNormal)), RimPower);
    color += rim * ColorModulator.rgb * EdgeDarken;

    // edge darkening
    float edge = smoothstep(0.0, EdgeWidth, n);
    color *= edge;

    // HDR and modulation
    color *= HDR * ColorModulator.rgb;
    color = clamp(color, 0.0, 1.0);

    // fog
    float fogFactor = clamp((vDepth - FogStart) / (FogEnd - FogStart), 0.0, 1.0);
    vec3 finalColor = mix(color, FogColor.rgb, fogFactor);

    fragColor = vec4(finalColor, 1.0);
}