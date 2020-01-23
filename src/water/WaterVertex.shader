#version 400 core

in vec3 v_Position;

uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjectionMatrix;

out vec4 f_ClipSpace;

void main(void)
{
    gl_Position = u_ProjectionMatrix * u_ViewMatrix * u_ModelMatrix * vec4(v_Position.x, 0.0f, v_Position.y, 1.0f);
    f_ClipSpace = gl_Position;
}