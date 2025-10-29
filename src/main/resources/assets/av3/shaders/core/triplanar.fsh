#version 150
uniform sampler2D TextureSampler;

in vec3 worldPos;
in vec3 worldNormal;

out vec4 fragColor;

vec2 projX(vec3 p) { return p.zy; }
vec2 projY(vec3 p) { return p.xz; }
vec2 projZ(vec3 p) { return p.xy; }

void main() {
    vec3 n = abs(normalize(worldNormal));
    n /= (n.x + n.y + n.z + 1e-6);

    float scale = 2.0; // tiling scale

    vec4 texX = texture(TextureSampler, projX(worldPos) * scale);
    vec4 texY = texture(TextureSampler, projY(worldPos) * scale);
    vec4 texZ = texture(TextureSampler, projZ(worldPos) * scale);

    fragColor = texX * n.x + texY * n.y + texZ * n.z;
}
