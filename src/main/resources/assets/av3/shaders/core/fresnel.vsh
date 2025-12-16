#version 330 core

#moj_import <fog.glsl>
#moj_import <photon:particle.glsl>

uniform sampler2D Sampler2;

uniform mat4 ModelViewMatrix;
uniform mat4 ProjtMat;
uniform int FogShape;

out float vertexDistance;
out vec2 textCoord0;
out vec4 vertexColor;

void main() {
    ParticleData data = getParticleData();

    gl_position = ProjtMat * ModelViewMatrix * vec4(data.Position, 1.0);

    vertexDistance = fog_distance(data.Position, FogShape);
    textCoord0 = data.UV;
    vertexColor = data.Color * texelFetch(Sampler2, data.LightUV / 16, 0)
    
}