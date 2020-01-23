#version 400 core

in vec3 v_Position;
in vec2 v_TextureCoords;
in vec3 v_Normal;

uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjectionMatrix;
uniform mat3 u_NormalMatrix;

uniform float u_UseFakeLighting;
uniform vec3 u_LightPosition[4];

uniform float u_NumberOfRows;
uniform vec2 u_Offset;

uniform vec4 u_ClipPlane;

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

    //clip horizontal
    gl_ClipDistance[0] = dot(worldPosition, u_ClipPlane);

    //use fake lighting check
    vec3 finalNormal = v_Normal;
    if(u_UseFakeLighting > 0.5f)
    {
        finalNormal = vec3(0.0f, 1.0f, 0.0f);
    }

    //calculate f_ToLightVector for multiple lights
    for(int i = 0; i < 4; i++)
    {
        f_ToLightVector[i] = u_LightPosition[i] - worldPosition.xyz;
    }

    f_TextureCoords = (v_TextureCoords / u_NumberOfRows) + u_Offset;
    f_SurfaceNormal = u_NormalMatrix * finalNormal;
    f_ToCameraVector = normalize((inverse(u_ViewMatrix) * vec4(0.0f, 0.0f, 0.0f, 1.0f)).xyz - worldPosition.xyz);

    //visibility modifier for fog in fragment shader
    float distanceRelativeToCam = length(positionRelativeToCam.xyz);
    f_Visibility = clamp(exp(-pow((distanceRelativeToCam * density), gradient)), 0.0f, 1.0f);
}