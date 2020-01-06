package textures;

public class ModelTexture
{
    public int textureID;

    private float shininess = 1.0f;
    private float reflectivity = 0.0f;

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
    public void SetShininess(float shininess) { this.shininess = shininess; }
    public void SetReflectivity(float reflectivity) { this.reflectivity = reflectivity; }
}
