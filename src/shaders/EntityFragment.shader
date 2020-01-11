#version 400 core

struct Material
{
    float shininess;
    float reflectivity;
};

in vec3 f_ToLightVector[4];
in vec2 f_TextureCoords;
in vec3 f_SurfaceNormal;
in vec3 f_ToCameraVector;
in float f_Visibility;

uniform sampler2D u_TextureSampler;
uniform vec3 u_SkyColor;

uniform Material u_Material;
uniform vec3 u_LightColor[4];
uniform vec3 u_Attenuation[4];

out vec4 out_Color;

void main(void)
{
    //transparency handler
    vec4 textureColor = texture(u_TextureSampler, f_TextureCoords);
    if(textureColor.a < 0.5)
    {
        discard;
    }

    vec3 totalDiffuse = vec3(0.0f);
    vec3 totalSpecular = vec3(0.0f);

    for(int i = 0; i < 4; i++)
    {
        //distance calculation for attenuation factor
        float distance = length(f_ToLightVector[i]);
        float attenuationFactor = u_Attenuation[i].x + (u_Attenuation[i].y * distance) + (u_Attenuation[i].z * distance * distance);

        vec3 unitToLightVector = normalize(f_ToLightVector[i]);

        //diffuse lighting
        vec3 normalVector = normalize(f_SurfaceNormal);
        float diff = max(dot(normalVector, unitToLightVector), 0.0f);

        //specular lighting
        vec3 reflectDirection = reflect(-unitToLightVector, f_SurfaceNormal);
        float spec = pow(max(dot(f_ToCameraVector, reflectDirection), 0.0f), u_Material.shininess);

        totalDiffuse = totalDiffuse + (u_LightColor[i] * diff) / attenuationFactor;
        totalSpecular = totalSpecular + (u_LightColor[i] * spec * u_Material.reflectivity) / attenuationFactor;
    }

    //ambient lighting
    totalDiffuse = max(totalDiffuse, 0.1f);

    //output color
    out_Color = vec4(totalDiffuse + totalSpecular, 1.0f) * textureColor;
    //fog color mix
    out_Color = mix(vec4(u_SkyColor, 1.0f), out_Color, f_Visibility);
}