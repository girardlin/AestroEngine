#version 400 core

in vec3 v_Position;

uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjectionMatrix;

out vec3 f_TextureCoords;

void main(void)
{
    gl_Position = u_ProjectionMatrix * u_ViewMatrix * vec4(v_Position, 1.0);
    f_TextureCoords = v_Position;
}