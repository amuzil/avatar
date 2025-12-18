#version 150

#moj_import <fog.glsl>
#moj_import <photon:particle.glsl>

uniform sampler2D Sampler2;   // lightmap (already applied in vertexColor in your vsh)
uniform sampler2D GradientTex;
uniform sampler2D NoiseTex;

uniform vec4 ColorModulator;

uniform float FogStart;
uniform float FogEnd;
uniform vec4  FogColor;

uniform float GameTime;

uniform float Bands;
uniform float HDR;

uniform float RimPower;
uniform float EdgeWidth;
uniform float EdgeDarken;

uniform float NoiseScale;
uniform float NoiseStrength;
uniform float NoiseSpeed;

in float vertexDistance;
in vec2  texCoord0;
in vec4  vertexColor;

in vec3 vPosVS;
in vec3 vNormalVS;

out vec4 fragColor;

float safe01(float x) { return clamp(x, 0.0, 1.0); }

void main() {
    // Base texture * vertexColor * ColorModulator
    vec4 base = texture(Sampler2, texCoord0) * vertexColor * ColorModulator;
    if (base.a <= 0.01) discard;

    // --- Flow distortion (tiny) ---
    vec2 nUV = texCoord0 * NoiseScale + vec2(GameTime * NoiseSpeed, GameTime * NoiseSpeed * 0.7);
    float n  = texture(NoiseTex, nUV).r * 2.0 - 1.0;
    vec2  duv = vec2(n, -n) * NoiseStrength;

    // --- View vectors ---
    vec3 V = normalize(-vPosVS);

    // If normals are junk (length ~0), fall back to camera-facing.
    vec3 N = vNormalVS;
    float nLen2 = dot(N, N);
    if (nLen2 < 1e-6) N = vec3(0.0, 0.0, 1.0);
    else N = normalize(N);

    float ndv = safe01(dot(N, V));

    // Rim is your “air edge” signal
    float rim = pow(1.0 - ndv, max(RimPower, 0.001));

    // Band the rim (this is where your old code likely went out of range)
    float steps = max(Bands, 1.0);
    float f = safe01(rim);

    float bandT;
    if (steps <= 1.0) {
        bandT = f;
    } else {
        float idx = floor(f * steps);
        idx = min(idx, steps - 1.0);
        bandT = idx / (steps - 1.0);
    }

    // Sample gradient along X; keep Y fixed midline; apply slight flow distortion
    vec2 gUV = vec2(safe01(bandT + duv.x), 0.5);
    vec3 grad = texture(GradientTex, gUV).rgb;

    // Edge darken (stylized contour)
    float edge = smoothstep(1.0 - EdgeWidth, 1.0, f);
    grad *= mix(1.0, 1.0 - EdgeDarken, edge);

    vec3 rgb = grad * HDR;

    // Preserve alpha from base texture (so your sprite/mesh cutout works)
    vec4 outCol = vec4(rgb, base.a);

    outCol = linear_fog(outCol, vertexDistance, FogStart, FogEnd, FogColor);
    fragColor = outCol;
}