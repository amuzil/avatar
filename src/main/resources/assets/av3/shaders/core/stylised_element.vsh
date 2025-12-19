#version 330 core

#moj_import <fog.glsl>
#moj_import <photon:particle.glsl>
#moj_import <photon:particle_utils.glsl>

uniform sampler2D Sampler2;
uniform sampler2D NoiseTex;
uniform sampler2D WaveTex;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;
//wave height, use for ocean waves
uniform float wave_height = 0.5;
uniform float surface_speed = 1.0;
//Tiling frequency of the noise accross the mesh
uniform float horizontal_frequency = 2.0;
uniform float vertical_frequency = 2.0;
uniform float spin = 0.5; //Twisting motion of the water
//water surface height variation based on the noise texture
uniform float texture_height = 0.5;
uniform int worldTime;
uniform int timeLoop = 300;
uniform float time_speed = 1.0;

out float vertexDistance;
out vec2 texCoord0;
out vec4 vertexColor;

out vec3 viewDirection;
out vec3 normal;
out vec2 uv;

void main() {
    ParticleData data = getParticleData();

    // Use Photon UV (this is your "UV" replacement)
    vec2 uv = data.UV;

    // Time: GameTime is 0..1 over a Minecraft day. Scale to taste.
    float time = -(worldTime % timeLoop) * time_speed;

    // Sample textures in vertex stage.
    // Prefer textureLod to avoid implicit mip LOD issues in vertex shaders. :contentReference[oaicite:3]{index=3}
    float wave = textureLod(WaveTex,
    vec2(uv.x + time * surface_speed, uv.y + time * surface_speed),
    0.0).r;

    float n = textureLod(NoiseTex,
    vec2(uv.x * horizontal_frequency + spin * (time * 0.5),
    uv.y * vertical_frequency + time),
    0.0).r;

    // Displace in world space along particle normal
    vec3 posWS = data.Position + data.Normal * (wave_height * wave + texture_height * n);

    // Now use posWS for transforms
    vec4 viewPos = ModelViewMat * vec4(posWS, 1.0);
    gl_Position = ProjMat * viewPos;

    // Use displaced position for fog so it matches what you see
    vertexDistance = fog_distance(posWS, FogShape);

    // Pass UV down to fragment normally
    texCoord0 = uv;
}