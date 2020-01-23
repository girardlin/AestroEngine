package skybox;

import entities.Camera;
import models.RawModel;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.Loader;

public class SkyboxRenderer
{
    private static final float SIZE = 500.0f;
    private static final float[] VERTICES = { -SIZE,  SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE,  SIZE, -SIZE, -SIZE,  SIZE, -SIZE, -SIZE, -SIZE,  SIZE, -SIZE, -SIZE, -SIZE, -SIZE,  SIZE, -SIZE, -SIZE,  SIZE, -SIZE, -SIZE,  SIZE,  SIZE, -SIZE, -SIZE,  SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE,  SIZE, SIZE,  SIZE,  SIZE, SIZE,  SIZE,  SIZE, SIZE,  SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE,  SIZE, -SIZE,  SIZE,  SIZE, SIZE,  SIZE,  SIZE, SIZE,  SIZE,  SIZE, SIZE, -SIZE,  SIZE, -SIZE, -SIZE,  SIZE, -SIZE,  SIZE, -SIZE, SIZE,  SIZE, -SIZE, SIZE,  SIZE,  SIZE, SIZE,  SIZE,  SIZE, -SIZE,  SIZE,  SIZE, -SIZE,  SIZE, -SIZE, -SIZE, -SIZE, -SIZE, -SIZE, -SIZE,  SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE,  SIZE, SIZE, -SIZE,  SIZE };

    private static final String[] TEXTURE_FILENAMES = {"right.png", "left.png", "top.png", "bottom.png", "back.png", "front.png"};

    private static final float ROTATE_SPEED = 0.5f;
    private float rotation = 0;

    private RawModel cube;
    private int textureID;
    private SkyboxShader shader;

    public SkyboxRenderer(Loader loader, Matrix4f projectionMatrix)
    {
        cube = loader.LoadToVAO(VERTICES, 3);
        textureID = loader.LoadCubeMap(TEXTURE_FILENAMES);

        shader = new SkyboxShader();

        //projection matrix uniform - will remain consistent, so we can just set it here, same with texture units
        shader.Bind();
        shader.SetUniformMat4f("u_ProjectionMatrix", projectionMatrix);
        shader.SetUniform1i("u_CubeMap", 0);
        shader.Unbind();
    }

    public void Render(Camera camera, Vector3f fogColor, float deltaTime)
    {
        shader.Bind();

        GL30.glBindVertexArray(cube.GetVaoID());
        GL20.glEnableVertexAttribArray(0);

        //texture enable
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID);

        //modified view matrix to remove translation column values
        rotation += ROTATE_SPEED * deltaTime;

        Matrix4f modifiedViewMatrix = camera.GetViewMatrix();
        modifiedViewMatrix.m30(0.0f);
        modifiedViewMatrix.m31(0.0f);
        modifiedViewMatrix.m32(0.0f);
        modifiedViewMatrix.rotate((float)Math.toRadians(rotation), 0.0f, 1.0f, 0.0f);

        shader.SetUniformMat4f("u_ViewMatrix", modifiedViewMatrix);
        shader.SetUniform3f("u_FogColor", fogColor);

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.GetVertexCount());

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

        shader.Unbind();
    }

    public void CleanUp()
    {
        shader.CleanUp();
    }
}
