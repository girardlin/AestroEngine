package water;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import toolBox.AestroMath;

public class WaterTile
{
    private static final float TILE_SIZE = 40.0f;

    private float height;
    private float x, z;

    public WaterTile(float centerX, float centerZ, float height)
    {
        this.x = centerX;
        this.z = centerZ;
        this.height = height;
    }

    public float GetTileSize() { return TILE_SIZE; }
    public float GetHeight() { return height; }
    public float GetX() { return x; }
    public float GetZ() { return z; }
    public Matrix4f GetModelMatrix()
    {
        return AestroMath.CreateModelMatrix(new Vector3f(x, height, z), new Vector3f(0.0f), new Vector3f(TILE_SIZE));
    }
}
