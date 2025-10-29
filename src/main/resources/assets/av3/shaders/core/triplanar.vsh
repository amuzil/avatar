#version 150
in vec3 Position;
in vec4 Color;
in vec2 UV0;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec4 ColorModulator;

out vec3 worldPos;
out vec3 normal;
out vec4 vertexColor;

void main() {

    // Proper transformation for screen rendering
    vec4 viewPos = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * viewPos;

    // Keep untransformed coordinates for triplanar texturing
    worldPos = Position;

    // Transform normals properly for lighting
    normal = normalize((ModelViewMat * vec4(Normal, 0.0)).xyz);
    vertexColor = Color;
}