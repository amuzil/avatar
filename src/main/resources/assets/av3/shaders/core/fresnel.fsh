#version 330

#moj_import <fog.glsl>
#moj_import <photon:particle.glsl>

uniform vec4 ColorModulator;
uniform sampler2D SamplerSceneDepth;
uniform vec2 ScreenSize;

uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float FresnelIntensity;
// HDR
uniform vec4 HDRFresnelColor;

uniform mat4 U_InverseProjectionMatrix;
uniform float Dist;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;
in vec3 ViewDir;
in vec3 ViewNormal;
in vec3 ViewPos;

out vec4 fragColor;

float FresnelEffect(vec3 Normal, vec3 ViewDir, float power) {
    float cosTheta = clamp(dot(normalize(Normal), normalize(ViewDir)), 0.0, 1.0);
    return pow(1.0 - cosTheta, power);
}

void main() {
    float fresnel = FresnelEffect(ViewNormal, ViewDir, FresnelIntensity);
    vec4 color = linear_fog(vertexColor * ColorModulator, vertexDistance, FogStart,
        FogEnd, FogColor);
    color.xyz *= HDRFresnelColor.rgb * HDRFresnelColor.a;

    vec2 screenUV = gl_FragCoord.xy / ScreenSize;
    float depth = texture(SamplerSceneDepth, screenUV).r;
    vec3 ndc;
    ndc.xy = screenUV * 2.0 - 1.0;
    ndc.z = depth * 2.0 - 1.0;
    vec4 clipSpacePos = vec4(ndc, 1.0);
    vec4 viewSpacePos = U_InverseProjectionMatrix * clipSpacePos;
    viewSpacePos /= viewSpacePos.w;

    float sceneViewDepth = length(viewSpacePos.xyz);
    float viewDepth = length(ViewPos);
    float subDist = sceneViewDepth - viewDepth - Dist;
    float a = clamp(fresnel + smoothstep(0, 1, 1 - subDist), 0, 1);


    fragColor = vec4(color.xyz, fresnel);
}