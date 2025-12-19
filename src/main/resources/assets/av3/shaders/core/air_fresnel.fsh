#version 330 core

#moj_import <fog.glsl>
#moj_import <photon:particle.glsl>

uniform vec4 ColorModulator;
uniform sampler2D SamplerSceneDepth;

uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float FresnelIntensity;
// HDR
uniform vec4 HDRFresnelColor;



in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;
in vec3 ViewDir;
in vec3 ViewNormal;
in vec3 ViewPos;

out vec4 fragColor;
void main() {
    float fresnel = pow(1.0 - clamp(dot(normalize(ViewNormal), normalize(ViewDir)), 0.0, 1.0), FresnelIntensity);
    vec4 color = linear_fog(vertexColor * ColorModulator, vertexDistance, FogStart,
        FogEnd, FogColor);
    color.xyz *= HDRFresnelColor.rgb * HDRFresnelColor.a;
    fragColor = vec4(color.xyz, fresnel);
}