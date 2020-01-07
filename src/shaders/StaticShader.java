package shaders;

import entities.Light;
import textures.ModelTexture;

public class StaticShader extends ShaderProgram
{
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

    public void SetLightUniforms(Light light)
    {
        SetUniform3f("u_Light.position", light.GetPosition());
        SetUniform3f("u_Light.color", light.GetColor());
    }

    public void SetShineUniforms(ModelTexture modelTexture)
    {
        SetUniform1f("u_Material.shininess", modelTexture.GetShininess());
        SetUniform1f("u_Material.reflectivity", modelTexture.GetReflectivity());
    }
}