package renderEngine;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import models.RawModel;
import objConverter.ModelData;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import textures.Texture;

public class Loader
{
	private List<Integer> vaoList = new ArrayList<Integer>();
	private List<Integer> vboList = new ArrayList<Integer>();
	private List<Integer> textureList = new ArrayList<Integer>();

	public RawModel LoadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices)
	{
		int vaoID = CreateVAO();
		CreateIndexBuffer(indices);
		StoreDataInAttributeList(0, 3, positions);
		StoreDataInAttributeList(1, 2, textureCoords);
		StoreDataInAttributeList(2, 3, normals);
		UnbindVAO();
		return new RawModel(vaoID, indices.length);
	}

	public RawModel LoadToVAO(ModelData modelData)
	{
		return this.LoadToVAO(modelData.GetVertices(), modelData.GetTextureCoords(), modelData.GetNormals(), modelData.GetIndices());
	}
	
	private int CreateVAO()
	{
		int vaoID =  GL30.glGenVertexArrays();
		vaoList.add(vaoID);
		BindVAO(vaoID);
		return vaoID;
	}

	private void CreateIndexBuffer(int[] indices)
	{
		int vboID = GL15.glGenBuffers();
		vboList.add(vboID);
		BindIndexBuffer(vboID);

		IntBuffer buffer = StoreDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}

	public int LoadTexture(String fileName)
	{
		Texture texture = new Texture(fileName);
		textureList.add(texture.GetTextureID());
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		return texture.GetTextureID();
	}

	private void StoreDataInAttributeList(int attributeNumber, int coordinateSize, float[] data)
	{
		int vboID = GL15.glGenBuffers();
		vboList.add(vboID);
		BindBuffer(vboID);

		FloatBuffer buffer = StoreDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		
		UnbindBuffer();
	}

	private FloatBuffer StoreDataInFloatBuffer(float[] data)
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private IntBuffer StoreDataInIntBuffer(int[] data)
	{
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	private void BindBuffer(int id) { GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id); }
	private void BindIndexBuffer(int id) { GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, id); }
	private void BindVAO(int id) { GL30.glBindVertexArray(id); }

	private void UnbindBuffer() { GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); }
	private void UnbindIndexBuffer() { GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0); }
	private void UnbindVAO()
	{
		GL30.glBindVertexArray(0);
	}

	public void CleanUp()
	{
		UnbindBuffer();
		UnbindIndexBuffer();
		UnbindVAO();

		for(int vao:vaoList) { GL30.glDeleteVertexArrays(vao); }
		for(int vbo:vboList) { GL15.glDeleteBuffers(vbo); }
		for(int texture:textureList) { GL11.glDeleteTextures(texture); }
	}
}