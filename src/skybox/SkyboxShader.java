package skybox;

import shaders.ShaderProgram;

public class SkyboxShader extends ShaderProgram
{
    private static final String VERTEX_FILE = "src/skybox/SkyboxVertex.shader";
    private static final String FRAGMENT_FILE = "src/skybox/SkyboxFragment.shader";

    public SkyboxShader()
    {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void BindAttribute()
    {
        super.BindAttribute(0, "v_Position");
    }
}
