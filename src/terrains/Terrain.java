package terrains;

import models.RawModel;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import renderEngine.Loader;
import textures.ModelTexture;
import toolBox.AestroMath;

public class Terrain
{
    private static final float SIZE = 80;
    private static final int VERTEX_COUNT = 128;

    private float x;
    private float z;
    private RawModel rawModel;
    private ModelTexture texture;

    public Terrain(int gridX, int gridZ, Loader loader, ModelTexture texture)
    {
        this.texture = texture;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.rawModel = generateTerrain(loader);
    }

    //thin's generate terrain method
    private RawModel generateTerrain(Loader loader){
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        int vertexPointer = 0;
        for(int i = 0; i < VERTEX_COUNT; i++){
            for(int j = 0; j < VERTEX_COUNT; j++){
                vertices[vertexPointer * 3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
                vertices[vertexPointer * 3 + 1] = 0;
                vertices[vertexPointer * 3 + 2 ] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
                normals[vertexPointer * 3] = 0;
                normals[vertexPointer * 3 + 1] = 1;
                normals[vertexPointer * 3 + 2 ] = 0;
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

    public float GetX() { return x; }
    public float GetZ() { return z; }
    public RawModel GetRawModel() { return rawModel; }
    public ModelTexture GetTexture() { return texture; }
    public Matrix4f GetModelMatrix()
    {
        return AestroMath.CreateModelMatrix(new Vector3f(x, 0.0f, z), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f));
    }
}
