#version 400 core

in vec3 v_Position;
in vec2 v_TextureCoords;
in vec3 v_Normal;

out vec3 f_FragPosition;
out vec2 f_TextureCoords;
out vec3 f_SurfaceNormal;
out vec3 f_ToCameraVector;
out float f_Visibility;

uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjectionMatrix;
uniform mat3 u_NormalMatrix;
uniform float u_UseFakeLighting;

const float density = 0.08f;
const float gradient = 1.25f;

void main(void)
{
    vec4 worldPosition =  u_ModelMatrix * vec4(v_Position, 1.0f);
    vec4 positionRelativeToCam = u_ViewMatrix * worldPosition;
    gl_Position = u_ProjectionMatrix * positionRelativeToCam;

    //use fake lighting check
    vec3 finalNormal = v_Normal;
    if(u_UseFakeLighting > 0.5f)
    {
        finalNormal = vec3(0.0f, 1.0f, 0.0f);
    }

    f_FragPosition = vec3(u_ModelMatrix * vec4(v_Position, 1.0f));
    f_TextureCoords = v_TextureCoords;
    f_SurfaceNormal = u_NormalMatrix * finalNormal;
    f_ToCameraVector = normalize((inverse(u_ViewMatrix) * vec4(0.0f, 0.0f, 0.0f, 1.0f)).xyz - worldPosition.xyz);

    //visibility modifier for fog in fragment shader
    float distanceRelativeToCam = length(positionRelativeToCam.xyz);
    f_Visibility = clamp(exp(-pow((distanceRelativeToCam * density), gradient)), 0.0f, 1.0f);
}