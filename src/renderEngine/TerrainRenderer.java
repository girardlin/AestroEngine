package renderEngine;

import entities.Camera;
import entities.Light;
import models.RawModel;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shaders.TerrainShader;
import terrains.Terrain;
import textures.TerrainTexturePack;

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

    public void Render(List<Terrain> terrains, Light sun, Camera camera, Vector3f skyColor)
    {   //uniforms that apply to every entity
        shader.SetLightUniforms(sun);                           //set lighting uniforms using passed in sun value
        shader.SetUniform3f("u_SkyColor", skyColor);      //fog uniform

        //render every processed terrain
        for (Terrain terrain : terrains)
        {
            PrepareTerrain(terrain, camera);
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.GetRawModel().GetVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            UnbindTerrain();
        }
    }

    private void PrepareTerrain(Terrain terrain, Camera camera)
    {   //sets VAO and textures
        RawModel rawModel = terrain.GetRawModel();
        //bind and enable VAO
        GL30.glBindVertexArray(rawModel.GetVaoID());
        //attribute activation
        for (int i = shader.GetAttributeCount(); i > 0; i--)
        {
            GL20.glEnableVertexAttribArray(i - 1);
        }
        //texture set
        BindTextures(terrain);

        //uniforms that apply to every model
        shader.SetShineUniforms();      //shininess and reflectivity uniforms
        shader.SetUniform1i("u_BackTexture", 0);
        shader.SetUniform1i("u_rTexture", 1);
        shader.SetUniform1i("u_gTexture", 2);
        shader.SetUniform1i("u_bTexture", 3);
        shader.SetUniform1i("u_BlendMap", 4);

        //generate MVP, normal matrix calculation
        Matrix4f modelMatrix = terrain.GetModelMatrix();
        Matrix4f viewMatrix = camera.GetViewMatrix();
        Matrix3f normalMatrix = new Matrix3f();
        modelMatrix.get3x3(normalMatrix);
        normalMatrix.invert().transpose();

        //uniforms that differ between every instance of an entity
        shader.SetUniformMat4f("u_ModelMatrix", modelMatrix);               //model matrix uniform
        shader.SetUniformMat4f("u_ViewMatrix", viewMatrix);                 //view matrix uniform
        shader.SetUniformMat4f("u_ProjectionMatrix", projectionMatrix);     //projection matrix uniform
        shader.SetUniformMat3f("u_NormalMatrix", normalMatrix);             //normal matrix uniform
    }

    private void BindTextures(Terrain terrain)
    {
        TerrainTexturePack texturePack = terrain.GetTexturePack();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.GetBackTexture().GetTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.GetrTexture().GetTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.GetgTexture().GetTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.GetbTexture().GetTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, terrain.GetBlendMap().GetTextureID());
    }

    private void UnbindTerrain()
    {   //unbind VAO and VAO attributes
        GL30.glBindVertexArray(0);
        //attribute deactivation
        for (int i = shader.GetAttributeCount(); i > 0; i--)
        {
            GL20.glDisableVertexAttribArray(i - 1);
        }
    }
}
