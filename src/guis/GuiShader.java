package guis;

import shaders.ShaderProgram;

public class GuiShader extends ShaderProgram
{
    private static final String VERTEX_FILE = "src/guis/GuiVertex.shader";
    private static final String FRAGMENT_FILE = "src/guis/GuiFragment.shader";

    public GuiShader()
    {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void BindAttribute()
    {
        super.BindAttribute(0, "v_Position");
    }
}