#version 400 core

in vec3 v_Position;
in vec2 v_TextureCoords;
in vec3 v_Normal;

out vec3 f_FragPosition;
out vec2 f_TextureCoords;
out vec3 f_SurfaceNormal;
out vec3 f_ToCameraVector;

uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjectionMatrix;
uniform mat3 u_NormalMatrix;

void main(void)
{
    vec4 worldPosition =  u_ModelMatrix * vec4(v_Position, 1.0f);
    gl_Position = u_ProjectionMatrix * u_ViewMatrix * worldPosition;

    f_FragPosition = vec3(u_ModelMatrix * vec4(v_Position, 1.0f));
    f_TextureCoords = v_TextureCoords;
    f_SurfaceNormal = u_NormalMatrix * v_Normal;
    f_ToCameraVector = normalize((inverse(u_ViewMatrix) * vec4(0.0f, 0.0f, 0.0f, 1.0f)).xyz - worldPosition.xyz);
}