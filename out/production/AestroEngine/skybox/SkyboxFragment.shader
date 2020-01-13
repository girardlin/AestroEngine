#version 400 core

in vec3 f_TextureCoords;

uniform samplerCube u_CubeMap;
uniform vec3 u_FogColor;

const float LOWER_LIMIT = 0.0f;
const float UPPER_LIMIT = 35.0f;

out vec4 out_Color;

void main(void)
{
    vec4 textureColor = texture(u_CubeMap, f_TextureCoords);

    float factor = (f_TextureCoords.y - LOWER_LIMIT) / (UPPER_LIMIT - LOWER_LIMIT);
    factor = clamp(factor, 0.0f, 1.0f);

    out_Color = mix(vec4(u_FogColor, 1.0f), textureColor, factor);
}