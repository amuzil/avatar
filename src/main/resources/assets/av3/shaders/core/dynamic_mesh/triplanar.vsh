#version 150
in vec3 Position;
in vec3 Normal;
in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec3 worldPos;
out vec3 worldNormal;

void main() {
    worldPos = Position;
    worldNormal = normalize(Normal);
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
}
