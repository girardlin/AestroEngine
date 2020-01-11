package shaders;

import entities.Light;
import org.joml.Vector3f;
import textures.ModelTexture;

import java.util.List;

public class StaticShader extends ShaderProgram
{
    private static final int MAX_LIGHTS = 4;

    private static final String VERTEX_FILE = "src/shaders/EntityVertex.shader";
    private static final String FRAGMENT_FILE = "src/shaders/EntityFragment.shader";

    public StaticShader()
    {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void BindAttribute()
    {
        super.BindAttribute(0, "v_Position");
        super.BindAttribute(1, "v_TextureCoords");
        super.BindAttribute(2, "v_Normal");
    }

    public void SetLightUniforms(List<Light> lights)
    {
        for(int i = 0; i < MAX_LIGHTS; i++)
        {
            if (i < lights.size())
            {
                SetUniform3f("u_LightPosition[" + i + "]", lights.get(i).GetPosition());
                SetUniform3f("u_LightColor[" + i + "]", lights.get(i).GetColor());
                SetUniform3f("u_Attenuation[" + i + "]", lights.get(i).GetAttenuation());
            }
            else
            {
                SetUniform3f("u_LightPosition[" + i + "]", new Vector3f(0.0f));
                SetUniform3f("u_LightColor[" + i + "]", new Vector3f(0.0f));
                SetUniform3f("u_Attenuation[" + i + "]", new Vector3f(1.0f, 0.0f, 0.0f));
            }
        }
    }

    public void SetShineUniforms(ModelTexture modelTexture)
    {
        SetUniform1f("u_Material.shininess", modelTexture.GetShininess());
        SetUniform1f("u_Material.reflectivity", modelTexture.GetReflectivity());
    }
}