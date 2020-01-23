package water;

import shaders.ShaderProgram;

public class WaterShader extends ShaderProgram
{
    private static final String VERTEX_FILE = "src/water/WaterVertex.shader";
    private static final String FRAGMENT_FILE = "src/water/WaterFragment.shader";

    public WaterShader()
    {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void BindAttribute()
    {
        super.BindAttribute(0, "v_Position");
    }
}
