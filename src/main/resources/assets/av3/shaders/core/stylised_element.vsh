#version 330 core

#moj_import <fog.glsl>
#moj_import <photon:particle.glsl>

uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;
uniform float GameTime;
uniform float TimeSpeed;

uniform vec3 Centre;

uniform float WaveScale;
uniform float WaveSpeed;
uniform float WaveStrength;
uniform sampler2D WaveTex;

uniform float NoiseScale;
uniform float NoiseSpeed;
uniform float NoiseStrength;
uniform sampler2D NoiseTex;

uniform vec3 NormalOverride;
out float vertexDistance;
out vec2 texCoord0;
out vec4 vertexColor;
out vec3 ViewDir;
out vec3 ViewNormal;
out vec3 ViewPos;
out vec3 LightDir;

void main() {
    ParticleData data = getParticleData();
    texCoord0 = data.UV;

    float time = GameTime * TimeSpeed;

    // in vertex shader
    vec3 p = data.Position;

    // world-space planar mapping (continuous)
    vec2 flowUV = p.xz * WaveScale + vec2(time * WaveSpeed);

    // sample displacement from flowUV (not texCoord0)
    float wave  = texture(WaveTex,  flowUV).r * 2.0 - 1.0;
    float noise = texture(NoiseTex, flowUV * NoiseScale).r * 2.0 - 1.0;
    float disp = wave * WaveStrength + noise * NoiseStrength;

    vec3 normal = data.Normal;
    if (NormalOverride != vec3(0))
        normal = NormalOverride;

    vec3 posWS = data.Position + normal * disp;

    // Compute view-space position
    vec4 ViewPos4 = ModelViewMat * vec4(posWS, 1.0);
    ViewPos  = ViewPos4.xyz;

    // Normal into view space
    ViewNormal = normalize(mat3(ModelViewMat) * normal);

    // View direction = from fragment toward camera (camera at 0,0,0 in view space)
    ViewDir = -normalize(ViewPos);
    LightDir = normalize((ModelViewMat * vec4(3.0, 10.0, 2.0, 0.0)).xyz);


    // Provided fog distance from view space
    vertexDistance = fog_distance(ViewPos4.xyz, FogShape);


    vertexColor = data.Color * texelFetch(Sampler2, data.LightUV / 16, 0);

    gl_Position = ProjMat * ViewPos4;

}