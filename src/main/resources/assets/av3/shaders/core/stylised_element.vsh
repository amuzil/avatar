#version 330 core

#moj_import <fog.glsl>
#moj_import <photon:particle.glsl>

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;

out vec3 vNormal;
out vec3 vViewDir;
out float vDepth;
out float vertexDistance;

void main() {
    ParticleData data = getParticleData();

    vec3 worldPos = (ModelViewMat * vec4(data.Position,1.0)).xyz;
    vNormal = normalize((ModelViewMat * vec4(data.Normal,0.0)).xyz);
    vViewDir = normalize(-worldPos);

    vDepth = length(worldPos);
    vertexDistance = fog_distance(data.Position, FogShape);

    gl_Position = ProjMat * vec4(worldPos,1.0);
}