#version 150

#moj_import <fog.glsl>
#moj_import <photon:particle.glsl>

uniform vec4 ColorModulator;
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

out vec4 fragColor;

float FresnelEffect(vec3 Normal, vec3 ViewDir, float power) {
    float cosTheta = clamp(dot(normalize(Normal), normalize(ViewDir)), 0.0, 1.0);
    return pow(1.0 - cosTheta, power);
}

void main() {
    float fresnel = FresnelEffect(ViewNormal, ViewDir, FresnelIntensity);
    vec4 color = linear_fog(vertexColor * ColorModulator, vertexDistance, FogStart, FogEnd, FogColor);
//    color.xyz = mix(color.xyz, HDRFresnelColor.xyz, fresnel);
    // OR
    color.xyz *= HDRFresnelColor.rgb * HDRFresnelColor.a;
    fragColor = vec4(color.xyz, fresnel);
}