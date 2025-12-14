#version 330 core

#moj_import <fog.glsl>
#moj_import <photon:particle.glsl>

uniform sampler2D Sampler2;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;

out float vertexDistance;
out vec2 texCoord0;
out vec4 vertexColor;

// View-space position & normal for banding
out vec3 vPosVS;
out vec3 vNormalVS;

void main() {
    ParticleData data = getParticleData();

    // World -> view
    vec4 viewPos = ModelViewMat * vec4(data.Position, 1.0);
    gl_Position = ProjMat * viewPos;

    vertexDistance = fog_distance(data.Position, FogShape);
    texCoord0 = data.UV;

    // Standard Photon lightmap fetch (same as example)
    vertexColor = data.Color * texelFetch(Sampler2, data.LightUV / 16, 0);

    vPosVS = viewPos.xyz;

    // Ignore non-uniform scale, good enough for VFX
    mat3 normalMat = mat3(ModelViewMat);
    vNormalVS = normalize(normalMat * data.Normal);
}