#version 330 core

#moj_import <fog.glsl>
#moj_import <photon:particle.glsl>
#moj_import <photon:particle_utils.glsl>

uniform sampler2D Sampler2;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;
//wave height, use for ocean waves
uniform float wave_height = 0.5;
//water surface height variation based on the noise texture
uniform float texture_height = 0.5;

out float vertexDistance;
out vec2 texCoord0;
out vec4 vertexColor;

out vec3 viewDirection;
out vec3 normal;
out vec2 uv;

void main() {
    ParticleData data = getParticleData();

    vec4 viewPos = ModelViewMat * vec4(data.Position, 1.0);
    gl_Position = ProjMat * viewPos;

    vertexDistance = fog_distance(data.Position, FogShape);
    texCoord0 = data.UV;
    vertexColor = data.Color * texelFetch(Sampler2, data.LightUV / 16, 0);

    viewDirection = normalize((ModelViewMat * vec4(data.Normal, 0.0)).xyz);
    normal = data.Normal;
    uv = data.UV;
}