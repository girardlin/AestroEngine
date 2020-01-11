package terrains;

import models.RawModel;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolBox.AestroMath;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Terrain
{
    private static final float SIZE = 80.0f;
    private static final float MAX_HEIGHT = 10.0f;
    private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;

    private float x;
    private float z;
    private RawModel rawModel;
    private TerrainTexturePack texturePack;
    private TerrainTexture blendMap;

    private float[][] heights;

    public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap, String heightMap)
    {
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.rawModel = GenerateTerrain(loader, heightMap);
        this.texturePack = texturePack;
        this.blendMap = blendMap;
    }

    //thin's generate terrain method
    private RawModel GenerateTerrain(Loader loader, String heightMap)
    {
        BufferedImage image = null;
        try
        {
            image = ImageIO.read(new File("res/" + heightMap));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        assert image != null;
        int VERTEX_COUNT = image.getHeight();
        heights = new float[VERTEX_COUNT][VERTEX_COUNT];

        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        int vertexPointer = 0;
        for(int i = 0; i < VERTEX_COUNT; i++){
            for(int j = 0; j < VERTEX_COUNT; j++){
                vertices[vertexPointer * 3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
                float height = GetHeight(j, i, image);
                heights[j][i] = height;
                vertices[vertexPointer * 3 + 1] = height;
                vertices[vertexPointer * 3 + 2 ] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
                Vector3f normal = CalculateNormal(j, i, image);
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2 ] = normal.z;
                textureCoords[vertexPointer * 2] = (float)j/((float)VERTEX_COUNT - 1);
                textureCoords[vertexPointer * 2 + 1] = (float)i/((float)VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz=0;gz<VERTEX_COUNT-1;gz++){
            for(int gx=0;gx<VERTEX_COUNT-1;gx++){
                int topLeft = (gz*VERTEX_COUNT)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.LoadToVAO(vertices, textureCoords, normals, indices);
    }

    private float GetHeight(int x, int y, BufferedImage image)
    {
        if (x < 0 || x >= image.getHeight() || y < 0 || y >= image.getHeight())
        {
            return 0.0f;
        }
        float height = image.getRGB(x, y);
        height += MAX_PIXEL_COLOR / 2.0f;
        height /= MAX_PIXEL_COLOR / 2.0f;
        height *= MAX_HEIGHT;

        return height;
    }

    private Vector3f CalculateNormal(int x, int y, BufferedImage image)
    {   //calculate height of neightboring vertices - needs optimization due to recalculating existing values
        float heightL = GetHeight(x - 1, y, image);
        float heightR = GetHeight(x + 1, y, image);
        float heightU = GetHeight(x, y + 1, image);
        float heightD = GetHeight(x - 1, y - 1, image);
        Vector3f normal = new Vector3f(heightL - heightR, 2.0f, heightD - heightU);
        normal.normalize();

        return normal;
    }

    public float GetHeightOfTerrain(float worldX, float worldZ)
    {
        //find player position relative to terrain coordinates
        float terrainX = worldX - this.x;
        float terrainZ = worldZ - this.z;
        //locate grid position of player on terrain
        float gridSquareSize = SIZE / ((float) heights.length - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
        //if player is out of bounds, set returned terrain height to 0
        if(gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0)
        {
            return 0;
        }
        //finds coordinates within grid square
        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
        float answer;
        if (xCoord <= (1 - zCoord))
        {
            answer = AestroMath.BarryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
                            heights[gridX + 1][gridZ], 0), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        }
        else
        {
            answer = AestroMath.BarryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
                            heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        }
        return answer;
    }

    public boolean InsideThisTerrain(float worldX, float worldZ)
    {
        return worldX > GetX() && worldX <= (GetX() + GetSize()) && worldZ > GetZ() && worldZ <= (GetZ() + GetSize());
    }

    public float GetX() { return x; }
    public float GetZ() { return z; }
    public float GetSize() { return SIZE; }
    public TerrainTexturePack GetTexturePack() { return texturePack; }
    public TerrainTexture GetBlendMap() { return blendMap; }
    public RawModel GetRawModel() { return rawModel; }
    public Matrix4f GetModelMatrix() { return AestroMath.CreateModelMatrix(new Vector3f(x, 0.0f, z), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f)); }
}
