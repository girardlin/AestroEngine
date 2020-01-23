package renderEngine;

import entities.Camera;
import entities.Light;
import models.RawModel;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
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

    public TerrainRenderer(Matrix4f projectionMatrix)
    {
        shader = new TerrainShader();

        //projection matrix uniform - will remain consistent, so we can just set it here, same with texture units
        shader.Bind();
        shader.SetUniformMat4f("u_ProjectionMatrix", projectionMatrix);
        shader.SetUniform1i("u_BackTexture", 0);
        shader.SetUniform1i("u_rTexture", 1);
        shader.SetUniform1i("u_gTexture", 2);
        shader.SetUniform1i("u_bTexture", 3);
        shader.SetUniform1i("u_BlendMap", 4);
        shader.Unbind();
    }

    public void Render(List<Terrain> terrains, List<Light> lights, Camera camera, Vector3f skyColor, Vector4f clipPlane)
    {
        shader.Bind();

        //uniforms that apply to every entity
        shader.SetLightUniforms(lights);                         //set lighting uniforms using passed in sun value
        shader.SetUniform3f("u_SkyColor", skyColor);      //fog uniform
        shader.SetUniform4f("u_ClipPlane", clipPlane);	//clipping plane uniform

        //render every processed terrain
        for (Terrain terrain : terrains)
        {
            PrepareTerrain(terrain, camera);
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.GetRawModel().GetVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            UnbindTerrain();
        }

        shader.Unbind();
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

        //uniforms that differ between every instance of an entity
        shader.SetUniformMat4f("u_ModelMatrix", terrain.GetModelMatrix());  //model matrix uniform
        shader.SetUniformMat4f("u_ViewMatrix", camera.GetViewMatrix());     //view matrix uniform
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

    public void CleanUp()
    {
        shader.CleanUp();
    }
}
