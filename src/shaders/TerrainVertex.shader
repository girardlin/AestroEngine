#version 400 core

in vec3 v_Position;
in vec2 v_TextureCoords;
in vec3 v_Normal;

uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjectionMatrix;

uniform vec3 u_LightPosition[4];

const float density = 0.08f;
const float gradient = 1.25f;

out vec3 f_ToLightVector[4];
out vec2 f_TextureCoords;
out vec3 f_SurfaceNormal;
out vec3 f_ToCameraVector;
out float f_Visibility;

void main(void)
{
    //gl_Position - MVP
    vec4 worldPosition =  u_ModelMatrix * vec4(v_Position, 1.0f);
    vec4 positionRelativeToCam = u_ViewMatrix * worldPosition;
    gl_Position = u_ProjectionMatrix * positionRelativeToCam;

    //calculate f_ToLightVector for multiple lights
    for(int i = 0; i < 4; i++)
    {
        f_ToLightVector[i] = u_LightPosition[i] - worldPosition.xyz;
    }

    f_TextureCoords = v_TextureCoords;
    f_SurfaceNormal = v_Normal;
    f_ToCameraVector = normalize((inverse(u_ViewMatrix) * vec4(0.0f, 0.0f, 0.0f, 1.0f)).xyz - worldPosition.xyz);

    //visibility modifier for fog in fragment shader
    float distanceRelativeToCam = length(positionRelativeToCam.xyz);
    f_Visibility = clamp(exp(-pow((distanceRelativeToCam * density), gradient)), 0.0f, 1.0f);
}