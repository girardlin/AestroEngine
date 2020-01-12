#version 400 core

in vec3 f_TextureCoords;

uniform samplerCube u_CubeMap;

out vec4 out_Color;

void main(void)
{
    out_Color = texture(u_CubeMap, f_TextureCoords);
}