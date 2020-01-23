package water;

import entities.Camera;
import models.RawModel;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.Loader;

import java.util.List;

public class WaterRenderer
{
    private RawModel quad;
    private WaterShader shader;
    private WaterFrameBuffers fbos;

    float[] vertices = { -1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1 };

    public WaterRenderer(Loader loader, Matrix4f projectionMatrix, WaterFrameBuffers fbos)
    {
        this.quad = loader.LoadToVAO(vertices, 2);
        this.shader = new WaterShader();
        this.fbos = fbos;

        //projection matrix uniform - will remain consistent, so we can just set it here, same with texture unit uniforms
        shader.Bind();
        shader.SetUniformMat4f("u_ProjectionMatrix", projectionMatrix);
        shader.SetUniform1i("u_ReflectionTexture", 0);
        shader.SetUniform1i("u_RefractionTexture", 1);
        shader.Unbind();
    }

    public void Render(List<WaterTile> water, Camera camera)
    {
        shader.Bind();

        GL30.glBindVertexArray(quad.GetVaoID());
        GL20.glEnableVertexAttribArray(0);

        //texture unit activation
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.GetReflectionTexture());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.GetRefractionTexture());

        //draw call
        for(WaterTile tile:water)
        {
            PrepareInstance(tile, camera);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.GetVertexCount());
        }

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

        shader.Unbind();
    }

    private void PrepareInstance(WaterTile tile, Camera camera)
    {
        shader.SetUniformMat4f("u_ModelMatrix", tile.GetModelMatrix());
        shader.SetUniformMat4f("u_ViewMatrix", camera.GetViewMatrix());
    }

    public void CleanUp()
    {
        shader.CleanUp();
    }
}
