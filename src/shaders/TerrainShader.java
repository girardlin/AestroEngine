package shaders;

import entities.Light;
import textures.TerrainTexture;

public class TerrainShader extends ShaderProgram
{
    private static final String VERTEX_FILE = "src/shaders/TerrainVertex.shader";
    private static final String FRAGMENT_FILE = "src/shaders/TerrainFragment.shader";

    public TerrainShader()
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

    public void SetShineUniforms()
    {
        SetUniform1f("u_Material.shininess", 1.0f);
        SetUniform1f("u_Material.reflectivity", 0.0f);
    }
}
