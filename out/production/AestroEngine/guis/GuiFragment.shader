#version 400 core

in vec2 f_TextureCoords;

out vec4 out_Color;

uniform sampler2D u_GuiTexture;

void main(void)
{
    out_Color = texture(u_GuiTexture, f_TextureCoords);
}