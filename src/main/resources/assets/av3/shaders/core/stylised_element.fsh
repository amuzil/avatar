#version 150

#moj_import <fog.glsl>
#moj_import <photon:particle_utils.glsl>

uniform sampler2D SamplerGradient;
uniform sampler2D NoiseTex;


uniform float GameTime;
uniform float Bands;
uniform float HDR;

uniform float FogStart;
uniform float FogEnd;
uniform vec4  FogColor;
uniform int FogShape;

uniform vec4 ColorModulator;
uniform float RimPower;
uniform float EdgeWidth;
uniform float EdgeDarken;

uniform float NoiseScale;
uniform float NoiseStrength;
uniform float NoiseSpeed;
uniform vec3 U_CameraPosition;

uniform float time_speed = 1.0;
//time specifically for the wave noise texture
uniform float surface_speed = 1.0;
uniform float spin = 0.5; //Twisting motion of the water
uniform float brightness = 0.6;
uniform float color_intensity = 0.0;
//Tiling frequency of the noise accross the mesh
uniform float horizontal_frequency = 2.0;
uniform float vertical_frequency = 2.0;
//overall size muliplier
uniform float size = 3.0;
//affects total size
uniform float banding_bias = 0.6;

uniform vec4 color1  = vec4(0.59, 0.761, 1.0, 1.0);
uniform vec4 color2  = vec4(0.274, 0.474, 0.98, 1.0);
uniform vec4 color3 = vec4(0.059, 0.389, 0.85, 1.0);
uniform vec4 color4  = vec4(0.0, 0.267, 1.0, 1.0);

in vec3 viewDirection;
in vec3 normal;
in float vDepth;
in float vertexDistance;
in vec2 uv;

out vec4 fragColor;

void main() {
    float time = GameTime * NoiseSpeed * -100;
    float normal_facing = dot(normal, viewDirection);
    float noise_value = texture(NoiseTex,vec2(uv.x * horizontal_frequency + spin * (time /2.0),
    (uv.y * vertical_frequency) + time)).r;

    normal_facing += (noise_value -0.5 + size) * 0.3;

    float band = normal_facing * 3.0 * banding_bias;
    vec4 band_color = vec4(0,0,0,0);
    if (band <= 1.5) {
        discard;
    }
    else if(band <= 2.0){
        band_color = mix(color1, color2, -0.01 / (band-2.01)); //Mixes the color bands to make a slight gradient
    }
    else if (band <= 2.5) {
        band_color = mix(color2, color3, -0.01 / (band-2.51));
    }
    else if (band <= 2.9) {
        band_color = mix(color3, color4, -0.01 / (band-2.91));
    }
    else if (band >= 0.0) {
        band_color = color4;
    }

    vec3 color = clamp(brightness * (vec3(1.0, 1.0, 1.0) - (band_color.xyz * -color_intensity)) * band_color.xyz, vec3(0.0, 0.0, 0.0), vec3(brightness, brightness, brightness));
    fragColor = linear_fog(vec4(color, 1.0) * ColorModulator, vertexDistance, FogStart, FogEnd, FogColor);
}