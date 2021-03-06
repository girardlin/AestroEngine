package renderEngine;

import entities.Entity;
import entities.Camera;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.*;
import shaders.StaticShader;
import textures.ModelTexture;

import java.util.List;
import java.util.Map;

public class EntityRenderer
{
	private StaticShader shader;

	public EntityRenderer(Matrix4f projectionMatrix)
	{
		shader = new StaticShader();

		//projection matrix uniform - will remain consistent, so we can just set it here, same with texture unit
		shader.Bind();
		shader.SetUniformMat4f("u_ProjectionMatrix", projectionMatrix);
		shader.SetUniform1i("u_TextureSampler", 0);
		shader.Unbind();
	}

	public void Render(Map<TexturedModel, List<Entity>> entities, List<Light> lights, Camera camera, Vector3f fogColor, Vector4f clipPlane)
	{
		shader.Bind();

		//uniforms that apply to every entity
		shader.SetLightUniforms(lights);						//set lighting uniforms using passed in sun value
		shader.SetUniform3f("u_SkyColor", fogColor);		//fog uniform
		shader.SetUniform4f("u_ClipPlane", clipPlane);	//clipping plane uniform

		//render every processed entity
		for(TexturedModel model:entities.keySet())
		{
			PrepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for(Entity entity:batch)
			{
				PrepareInstance(entity, camera);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.GetRawModel().GetVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			UnbindTexturedModel();
		}

		shader.Unbind();
	}

	private void PrepareTexturedModel(TexturedModel model)
	{ 	//sets VAO and textures
		RawModel rawModel = model.GetRawModel();
		//bind and enable VAO
		GL30.glBindVertexArray(rawModel.GetVaoID());
		//attribute activation
		for(int i = shader.GetAttributeCount(); i > 0; i--)
		{
			GL20.glEnableVertexAttribArray(i - 1);
		}
		//disable culling if texture is transparent
		ModelTexture texture = model.GetTexture();
		if(texture.IsTransparent())
		{
			MasterRenderer.DisableCulling();
		}
		//texture set
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, model.GetTexture().GetTextureID());

		//uniforms that apply to every model
		shader.SetShineUniforms(texture); 													//shininess and reflectivity uniforms
		shader.SetUniform1b("u_UseFakeLighting", texture.IsUsingFakeLighting());		//fake lighting uniform
		shader.SetUniform1f("u_NumberOfRows", model.GetTexture().GetNumberOfRows());	//number of rows for texture atlas uniform
	}

	private void PrepareInstance(Entity entity, Camera camera)
	{ 	//sets uniforms, transformations
		//normal matrix calculation
		Matrix4f modelMatrix = entity.GetModelMatrix();
		Matrix3f normalMatrix = new Matrix3f().identity();
		if (!entity.IsScaledEvenly())
		{
			modelMatrix.get3x3(normalMatrix);
			normalMatrix.invert().transpose();
		}

		//uniforms that differ between every instance of an entity
		shader.SetUniformMat4f("u_ModelMatrix", modelMatrix);									    //model matrix uniform
		shader.SetUniformMat4f("u_ViewMatrix", camera.GetViewMatrix());							//view matrix uniform
		shader.SetUniformMat3f("u_NormalMatrix", normalMatrix);								    //normal matrix uniform
		shader.SetUniform2f("u_Offset", entity.GetTextureXOffset(), entity.GetTextureYOffset());  //offset for texture atlas uniform
	}

	private void UnbindTexturedModel()
	{	//enable culling again, unbind VAO and VAO attributes
		MasterRenderer.EnableCulling();
		GL30.glBindVertexArray(0);
		//attribute deactivation
		for(int i = shader.GetAttributeCount(); i > 0; i--)
		{
			GL20.glDisableVertexAttribArray(i - 1);
		}
	}

	public void CleanUp()
	{
		shader.CleanUp();
	}
}