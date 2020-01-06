package renderEngine;

import entities.Entity;
import entities.Camera;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.*;
import shaders.StaticShader;

import java.util.List;
import java.util.Map;

public class EntityRenderer
{
	private StaticShader shader;
	private Matrix4f projectionMatrix;

	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix)
	{
		this.shader = shader;
		this.projectionMatrix = projectionMatrix;
	}

	public void Render(Map<TexturedModel, List<Entity>> entities, Camera camera, Light sun)
	{
		//set lighting uniforms using passed in sun value
		shader.SetLightUniforms(sun);

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
	}

	private void PrepareTexturedModel(TexturedModel model)
	{ 	//sets texture, VAO
		RawModel rawModel = model.GetRawModel();
		//bind and enable VAO
		GL30.glBindVertexArray(rawModel.GetVaoID());
		//attribute activation
		for(int i = shader.GetAttributeCount(); i > 0; i--)
		{
			GL20.glEnableVertexAttribArray(i - 1);
		}
		//shininess uniforms
		shader.SetShineUniforms(model.GetTexture());
		//texture set
		GL13.glActiveTexture(0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, model.GetTexture().GetTextureID());
	}

	private void UnbindTexturedModel()
	{
		//unbind VAO and VAO attributes
		GL30.glBindVertexArray(0);
		//attribute deactivation
		for(int i = shader.GetAttributeCount(); i > 0; i--)
		{
			GL20.glDisableVertexAttribArray(i - 1);
		}
	}

	private void PrepareInstance(Entity entity, Camera camera)
	{ 	//sets uniforms, transformations
		//generate MVP
		Matrix4f modelMatrix = entity.GetModelMatrix();
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
}