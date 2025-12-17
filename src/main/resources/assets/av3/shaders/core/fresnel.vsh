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
out vec3 ViewDir;
out vec3 ViewNormal;

out vec4 fragColor;

void main() {
    ParticleData data = getParticleData();

    // Compute view-space position
    vec4 viewPos4 = ModelViewMat * vec4(data.Position, 1.0);
    vec3 viewPos  = viewPos4.xyz;

    // Normal into view space
    ViewNormal = normalize(mat3(ModelViewMat) * data.Normal);

    // View direction = from fragment toward camera (camera at 0,0,0 in view space)
    ViewDir = normalize(-viewPos);

    // Provided fog distance from view space
    vertexDistance = fog_distance(viewPos, FogShape);

    texCoord0 = data.UV;
    vertexColor = data.Color * texelFetch(Sampler2, data.LightUV / 16, 0);

    gl_Position = ProjMat * viewPos4;

}