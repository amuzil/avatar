#version 330 core

#moj_import <fog.glsl>
#moj_import <photon:particle.glsl>

uniform sampler2D Sampler2; // lightmap
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;

out float vertexDistance;
out vec2  texCoord0;
out vec4  vertexColor;

out vec3 vPosVS;
out vec3 vNormalVS;

void main() {
    ParticleData data = getParticleData();

    vec4 viewPos = ModelViewMat * vec4(data.Position, 1.0);
    gl_Position = ProjMat * viewPos;

    vertexDistance = fog_distance(data.Position, FogShape);
    texCoord0 = data.UV;

    // Lightmap modulation like vanilla/Photon expects
    vertexColor = data.Color * texelFetch(Sampler2, data.LightUV / 16, 0);

    vPosVS = viewPos.xyz;

    // Try to use particle normal if Photon supplies it; if it’s zero the frag has a fallback.
    // If your ParticleData doesn't have Normal, replace with a constant vec3(0,0,1).
    mat3 normalMat = mat3(ModelViewMat);
    vNormalVS = normalMat * data.Normal;
}