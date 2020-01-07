#version 400 core

struct Material
{
    float shininess;
    float reflectivity;
};

struct Light
{
    vec3 position;
    vec3 color;
};

in vec3 f_FragPosition;
in vec2 f_TextureCoords;
in vec3 f_SurfaceNormal;
in vec3 f_ToCameraVector;
in float f_Visibility;

out vec4 out_Color;

uniform sampler2D u_BackTexture;
uniform sampler2D u_rTexture;
uniform sampler2D u_gTexture;
uniform sampler2D u_bTexture;
uniform sampler2D u_BlendMap;
uniform Material u_Material;
uniform Light u_Light;
uniform vec3 u_SkyColor;

void main(void)
{
    //blend map color - untiled coordinates
    vec4 blendMapColor = texture(u_BlendMap, f_TextureCoords);

    //back texture color and amount with tiled coordinates
    vec2 tiledCoords = f_TextureCoords * 80.0f;
    float backTextureAmount = 1.0f - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
    vec4 backTextureColor = texture(u_BackTexture, tiledCoords) * backTextureAmount;

    //texture color of red, green, and blue represented textures used in blendmap
    vec4 rTextureColor = texture(u_rTexture, tiledCoords) * blendMapColor.r;
    vec4 gTextureColor = texture(u_gTexture, tiledCoords) * blendMapColor.g;
    vec4 bTextureColor = texture(u_bTexture, tiledCoords) * blendMapColor.b;

    vec4 combinedColor = backTextureColor + rTextureColor + gTextureColor + bTextureColor;

    //ambient lighting
    vec3 ambient = u_Light.color * 0.2f;

    //diffuse lighting
    vec3 normalVector = normalize(f_SurfaceNormal);
    vec3 lightDirection = normalize(u_Light.position - f_FragPosition);

    float diff = max(dot(normalVector, lightDirection), 0.0f);
    vec3 diffuse = u_Light.color * diff;

    //specular lighting
    vec3 reflectDirection = reflect(-lightDirection, f_SurfaceNormal);

    float spec = pow(max(dot(f_ToCameraVector, reflectDirection), 0.0f), u_Material.shininess);
    vec3 specular = u_Light.color * spec * u_Material.reflectivity;

    //output color
    out_Color = vec4(ambient + diffuse + specular, 1.0f) * combinedColor;
    //fog color mix
    out_Color = mix(vec4(u_SkyColor, 1.0f), out_Color, f_Visibility);
}