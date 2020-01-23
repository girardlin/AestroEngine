#version 400 core

in vec4 f_ClipSpace;

uniform sampler2D u_ReflectionTexture;
uniform sampler2D u_RefractionTexture;

out vec4 out_Color;

void main(void)
{
    vec2 ndc = (f_ClipSpace.xy / f_ClipSpace.w) / 2.0f + 0.5f;
    vec2 reflectTexCoords = vec2(ndc.x, 1-ndc.y);
    vec2 refractTexCoords = vec2(ndc.x, ndc.y);

    vec4 reflectColor = texture(u_ReflectionTexture, reflectTexCoords);
    vec4 refractColor = texture(u_RefractionTexture, refractTexCoords);

    out_Color = mix(reflectColor, refractColor, 0.6f);
}