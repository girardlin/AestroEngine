#version 400 core

in vec2 f_TextureCoords;

uniform sampler2D u_GuiTexture;

out vec4 out_Color;

void main(void)
{
    out_Color = texture(u_GuiTexture, f_TextureCoords);
}