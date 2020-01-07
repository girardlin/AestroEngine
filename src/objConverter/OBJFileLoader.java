package objConverter;

//ThinMatrix's OBJ Loader

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class OBJFileLoader
{
	private static final String RES_LOC = "res/";

	public static ModelData loadOBJ(String objFileName)
	{
		FileReader isr = null;
		File objFile = new File(RES_LOC + objFileName);
		try
		{
			isr = new FileReader(objFile);
		}
		catch (FileNotFoundException e)
		{
			System.err.println("File not found in res; don't use any extension");
		}
		BufferedReader reader = new BufferedReader(isr);
		String line;
		List<Vertex> vertices = new ArrayList<Vertex>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		try
		{
			while (true)
			{
				line = reader.readLine();
				if (line.startsWith("v "))
				{
					String[] currentLine = line.split(" ");
					Vector3f vertex = new Vector3f((float) Float.parseFloat(currentLine[1]),
							(float) Float.parseFloat(currentLine[2]),
							(float) Float.parseFloat(currentLine[3]));
					Vertex newVertex = new Vertex(vertices.size(), vertex);
					vertices.add(newVertex);

				}
				else if (line.startsWith("vt "))
				{
					String[] currentLine = line.split(" ");
					Vector2f texture = new Vector2f((float) Float.parseFloat(currentLine[1]),
							(float) Float.parseFloat(currentLine[2]));
					textures.add(texture);
				}
				else if (line.startsWith("vn "))
				{
					String[] currentLine = line.split(" ");
					Vector3f normal = new Vector3f((float) Float.parseFloat(currentLine[1]),
							(float) Float.parseFloat(currentLine[2]),
							(float) Float.parseFloat(currentLine[3]));
					normals.add(normal);
				}
				else if (line.startsWith("f "))
				{
					break;
				}
			}
			while (line != null && line.startsWith("f "))
			{
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				processVertex(vertex1, vertices, indices);
				processVertex(vertex2, vertices, indices);
				processVertex(vertex3, vertices, indices);
				line = reader.readLine();
			}
			reader.close();
		}
		catch (IOException e)
		{
			System.err.println("Error reading the file");
		}
		removeUnusedVertices(vertices);
		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		float furthest = convertDataToArrays(vertices, textures, normals, verticesArray,
				texturesArray, normalsArray);
		int[] indicesArray = convertIndicesListToArray(indices);
		return new ModelData(verticesArray, texturesArray, normalsArray, indicesArray,
				furthest);
	}

	private static void processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices)
	{
		int index = Integer.parseInt(vertex[0]) - 1;
		Vertex currentVertex = vertices.get(index);
		int textureIndex = Integer.parseInt(vertex[1]) - 1;
		int normalIndex = Integer.parseInt(vertex[2]) - 1;
		if (!currentVertex.IsSet())
		{
			currentVertex.SetTextureIndex(textureIndex);
			currentVertex.SetNormalIndex(normalIndex);
			indices.add(index);
		}
		else
		{
			dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices, vertices);
		}
	}

	private static int[] convertIndicesListToArray(List<Integer> indices)
	{
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++)
		{
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}

	private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures,
			List<Vector3f> normals, float[] verticesArray, float[] texturesArray,
			float[] normalsArray)
	{
		float furthestPoint = 0;
		for (int i = 0; i < vertices.size(); i++)
		{
			Vertex currentVertex = vertices.get(i);
			if (currentVertex.GetLength() > furthestPoint)
			{
				furthestPoint = currentVertex.GetLength();
			}
			Vector3f position = currentVertex.GetPosition();
			Vector2f textureCoord = textures.get(currentVertex.GetTextureIndex());
			Vector3f normalVector = normals.get(currentVertex.GetNormalIndex());
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			texturesArray[i * 2] = textureCoord.x;
			texturesArray[i * 2 + 1] = 1 - textureCoord.y;
			normalsArray[i * 3] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;
		}
		return furthestPoint;
	}

	private static void dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex,
			int newNormalIndex, List<Integer> indices, List<Vertex> vertices)
	{
		if (previousVertex.HasSameTextureAndNormal(newTextureIndex, newNormalIndex))
		{
			indices.add(previousVertex.GetIndex());
		}
		else
		{
			Vertex anotherVertex = previousVertex.GetDuplicateVertex();
			if (anotherVertex != null)
			{
				dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex,
						indices, vertices);
			}
			else
			{
				Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.GetPosition());
				duplicateVertex.SetTextureIndex(newTextureIndex);
				duplicateVertex.SetNormalIndex(newNormalIndex);
				previousVertex.SetDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.GetIndex());
			}
		}
	}
	
	private static void removeUnusedVertices(List<Vertex> vertices)
	{
		for(Vertex vertex:vertices)
		{
			if(!vertex.IsSet())
			{
				vertex.SetTextureIndex(0);
				vertex.SetNormalIndex(0);
			}
		}
	}

}