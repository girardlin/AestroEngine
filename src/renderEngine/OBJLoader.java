package renderEngine;

import models.RawModel;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class OBJLoader
{
    public static RawModel LoadObjModel(String fileName, Loader loader)
    {
        FileReader fr = null;
        try
        {
            fr = new FileReader(new File("res/" + fileName));
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Couldn't load obj file!");
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(fr);
        String line;
        List<Vector3f> vertices = new ArrayList<Vector3f>();
        List<Vector2f> texCoords = new ArrayList<Vector2f>();
        List<Vector3f> normals = new ArrayList<Vector3f>();
        List<Integer> indices = new ArrayList<Integer>();
        float[] verticesArray = null;
        float[] texCoordsArray = null;
        float[] normalsArray = null;
        int[] indicesArray = null;
        try
        {   //handles vertex positions, texture coordinates, normals parsing
            while(true)
            {
                line = reader.readLine();
                String[] currentLine = line.split(" ");
                if (line.startsWith("v "))
                {
                    Vector3f vertexPosition = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    vertices.add(vertexPosition);
                }
                else if (line.startsWith("vt "))
                {
                    Vector2f textureCoordinates = new Vector2f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
                    texCoords.add(textureCoordinates);
                }
                else if (line.startsWith("vn "))
                {
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                }
                else if (line.startsWith("f "))
                {
                    texCoordsArray = new float[vertices.size()*2];
                    normalsArray = new float[vertices.size()*3];
                    break;
                }
            }
            //handles faces
            while(line != null)
            {
                if(!line.startsWith("f "))
                {
                    line = reader.readLine();
                    continue;
                }
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");

                ProcessVertex(vertex1, indices, texCoords, normals, texCoordsArray, normalsArray);
                ProcessVertex(vertex2, indices, texCoords, normals, texCoordsArray, normalsArray);
                ProcessVertex(vertex3, indices, texCoords, normals, texCoordsArray, normalsArray);
                line = reader.readLine();
            }

            reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        verticesArray = new float[vertices.size() * 3];
        indicesArray = new int[indices.size()];

        int vertexPointer = 0;
        for(Vector3f vertex:vertices)
        {
            verticesArray[vertexPointer++] = vertex.x;
            verticesArray[vertexPointer++] = vertex.y;
            verticesArray[vertexPointer++] = vertex.z;
        }

        for(int i = 0; i < indices.size(); i++)
        {
            indicesArray[i] = indices.get(i);
        }

        return loader.LoadToVAO(verticesArray, texCoordsArray, normalsArray, indicesArray);
    }

    private static void ProcessVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures, List<Vector3f> normals, float[] texCoordsArray, float[] normalsArray)
    {
        int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer);

        Vector2f currentTexCoord = textures.get(Integer.parseInt(vertexData[1]) - 1);
        texCoordsArray[currentVertexPointer * 2] = currentTexCoord.x;
        texCoordsArray[currentVertexPointer * 2 + 1] = 1 - currentTexCoord.y;

        Vector3f currentNormal = normals.get(Integer.parseInt(vertexData[2]) - 1);
        normalsArray[currentVertexPointer * 3] = currentNormal.x;
        normalsArray[currentVertexPointer * 3 + 1] = currentNormal.y;
        normalsArray[currentVertexPointer * 3 + 2] = currentNormal.z;
    }
}
