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

out vec4 out_Color;

uniform sampler2D u_TextureSampler;
uniform Material u_Material;
uniform Light u_Light;

void main(void)
{
    //ambient lighting
    vec3 ambient = u_Light.color * vec3(texture(u_TextureSampler, f_TextureCoords)) * 0.2f;

    //diffuse lighting
    vec3 normalVector = normalize(f_SurfaceNormal);
    vec3 lightDirection = normalize(u_Light.position - f_FragPosition);

    float diff = max(dot(normalVector, lightDirection), 0.0f);
    vec3 diffuse = u_Light.color * diff * vec3(texture(u_TextureSampler, f_TextureCoords));

    //specular lighting
    vec3 reflectDirection = reflect(-lightDirection, f_SurfaceNormal);

    float spec = pow(max(dot(f_ToCameraVector, reflectDirection), 0.0f), u_Material.shininess);
    vec3 specular = u_Light.color * spec * u_Material.reflectivity * vec3(texture(u_TextureSampler, f_TextureCoords));

    //output color
    out_Color = vec4(ambient + diffuse + specular, 1.0f);
}