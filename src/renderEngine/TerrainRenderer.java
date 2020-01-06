package renderEngine;

import entities.Camera;
import entities.Light;
import models.RawModel;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shaders.TerrainShader;
import terrains.Terrain;

import java.util.List;

public class TerrainRenderer
{
    private TerrainShader shader;
    private Matrix4f projectionMatrix;

    public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix)
    {
        this.shader = shader;
        this.projectionMatrix = projectionMatrix;
    }

    public void Render(List<Terrain> terrains, Camera camera, Light sun)
    {
        //set lighting uniforms using passed in sun value
        shader.SetLightUniforms(sun);

        //render every processed terrain
        for (Terrain terrain : terrains)
        {
            PrepareTerrain(terrain, camera);
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.GetRawModel().GetVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            UnbindTexturedModel();
        }
    }

    private void PrepareTerrain(Terrain terrain, Camera camera)
    {   //textures and VAO
        RawModel rawModel = terrain.GetRawModel();
        //bind and enable VAO
        GL30.glBindVertexArray(rawModel.GetVaoID());
        //attribute activation
        for (int i = shader.GetAttributeCount(); i > 0; i--)
        {
            GL20.glEnableVertexAttribArray(i - 1);
        }
        //shininess uniforms
        shader.SetShineUniforms(terrain.GetTexture());
        //texture set
        GL13.glActiveTexture(0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, terrain.GetTexture().GetTextureID());

        //matrices and uniforms
        Matrix4f modelMatrix = terrain.GetModelMatrix();
        Matrix4f viewMatrix = camera.GetViewMatrix();

        shader.SetUniformMat4f("u_ModelMatrix", modelMatrix);
        shader.SetUniformMat4f("u_ViewMatrix", viewMatrix);
        shader.SetUniformMat4f("u_ProjectionMatrix", projectionMatrix);

        //normal matrix calculation
        Matrix3f normalMatrix = new Matrix3f();
        modelMatrix.get3x3(normalMatrix);
        normalMatrix.invert().transpose();

        shader.SetUniformMat3f("u_NormalMatrix", normalMatrix);
    }

    private void UnbindTexturedModel()
    {
        //unbind VAO and VAO attributes
        GL30.glBindVertexArray(0);
        //attribute deactivation
        for (int i = shader.GetAttributeCount(); i > 0; i--)
        {
            GL20.glDisableVertexAttribArray(i - 1);
        }
    }
}
