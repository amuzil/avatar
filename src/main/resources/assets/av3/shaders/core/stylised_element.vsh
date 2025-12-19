#version 330 core

#moj_import <fog.glsl>
#moj_import <photon:particle.glsl>

uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;
uniform float GameTime;

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

out vec4 fragColor;

void main() {
    ParticleData data = getParticleData();
    texCoord0 = data.UV;

    float time = GameTime * -500;

    vec2 waveUV  = texCoord0 * WaveScale  + vec2(time * WaveSpeed);
    vec2 noiseUV = texCoord0 * NoiseScale + vec2(time * NoiseSpeed);

    // Vertex-stage sampling: lock to mip 0 to keep it stable
    float wave  = textureLod(WaveTex,  waveUV,  0.0).r * 2.0 - 1.0;
    float noise = textureLod(NoiseTex, noiseUV, 0.0).r * 2.0 - 1.0;

    float disp = wave * WaveStrength + noise * NoiseStrength;
    vec3 posWS = data.Position + data.Normal * disp;

    // Compute view-space position
    vec4 ViewPos4 = ModelViewMat * vec4(posWS, 1.0);
    ViewPos  = ViewPos4.xyz;

    // Normal into view space
    ViewNormal = normalize(mat3(ModelViewMat) * data.Normal);

    // View direction = from fragment toward camera (camera at 0,0,0 in view space)
    ViewDir = -normalize(ViewPos);

    // Provided fog distance from view space
    vertexDistance = fog_distance(ViewPos4.xyz, FogShape);


    vertexColor = data.Color * texelFetch(Sampler2, data.LightUV / 16, 0);

    gl_Position = ProjMat * ViewPos4;

}