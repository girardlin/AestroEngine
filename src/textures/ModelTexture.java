package textures;

public class ModelTexture
{
    public int textureID;

    private float shininess = 1.0f;
    private float reflectivity = 0.1f;

    private int numberOfRows = 1;

    boolean transparent = false;
    boolean usingFakeLighting = false;

    public ModelTexture(int id)
    {
        this.textureID = id;
    }
    public ModelTexture(int id, float shininess, float reflectivity)
    {
        this.textureID = id;
        this.shininess = shininess;
        this.reflectivity = reflectivity;
    }

    public int GetTextureID() { return textureID; }
    public float GetShininess() { return shininess; }
    public float GetReflectivity() { return reflectivity; }
    public int GetNumberOfRows() { return numberOfRows; }
    public boolean IsTransparent() { return transparent; }
    public boolean IsUsingFakeLighting() { return usingFakeLighting; }
    public void SetShininess(float shininess) { this.shininess = shininess; }
    public void SetReflectivity(float reflectivity) { this.reflectivity = reflectivity; }
    public void SetNumberOfRows(int numberOfRows) { this.numberOfRows = numberOfRows; }
    public void SetTransparencyBool(boolean bool) { transparent = bool; }
    public void SetUsingFakeLighting(boolean bool) { usingFakeLighting = bool; }
}
