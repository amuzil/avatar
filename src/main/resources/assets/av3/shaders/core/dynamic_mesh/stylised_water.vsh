#version 330 core

#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
// Texture UVs
in vec2 UV0;
// Light Map
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;
uniform float GameTime;
uniform float TimeSpeed;

uniform float WaveScale;
uniform float WaveSpeed;
uniform float WaveStrength;
uniform sampler2D WaveTex;

uniform float NoiseScale;
uniform float NoiseSpeed;
uniform float NoiseStrength;
uniform sampler2D NoiseTex;

out float vertexDistance;
out vec2 texCoord0;
out vec4 vertexColor;
out vec3 ViewDir;
out vec3 ViewNormal;
out vec3 ViewPos;

void main() {

    texCoord0 = UV0;

    float time = GameTime * TimeSpeed;

    // in vertex shader
    vec3 p = Position;

    // world-space planar mapping (continuous)
    vec2 flowUV = p.xz * WaveScale + vec2(time * WaveSpeed);

    // sample displacement from flowUV (not texCoord0)
    float wave  = texture(WaveTex,  flowUV).r * 2.0 - 1.0;
    float noise = texture(NoiseTex, flowUV * NoiseScale).r * 2.0 - 1.0;
    float disp = wave * WaveStrength + noise * NoiseStrength;

    vec3 posWS = p + Normal * disp;

    // Compute view-space position
    vec4 ViewPos4 = ModelViewMat * vec4(posWS, 1.0);
    ViewPos  = ViewPos4.xyz;

    // Normal into view space
    ViewNormal = normalize(mat3(ModelViewMat) * Normal);

    // View direction = from fragment toward camera (camera at 0,0,0 in view space)
    ViewDir = -normalize(ViewPos);

    // Provided fog distance from view space
    vertexDistance = fog_distance(ViewPos4.xyz, FogShape);


    vertexColor = Color * texelFetch(Sampler2, UV2 / 16, 0);

    gl_Position = ProjMat * ViewPos4;

}