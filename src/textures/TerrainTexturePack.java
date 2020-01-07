package textures;

public class TerrainTexturePack
{
    private TerrainTexture backTexture;
    private TerrainTexture rTexture;
    private TerrainTexture gTexture;
    private TerrainTexture bTexture;

    public TerrainTexturePack(TerrainTexture backTexture, TerrainTexture rTexture, TerrainTexture gTexture, TerrainTexture bTexture)
    {
        this.backTexture = backTexture;
        this.rTexture = rTexture;
        this.gTexture = gTexture;
        this.bTexture = bTexture;
    }

    public TerrainTexture GetBackTexture() { return backTexture; }
    public TerrainTexture GetrTexture() { return rTexture; }
    public TerrainTexture GetgTexture() { return gTexture; }
    public TerrainTexture GetbTexture() { return bTexture; }
}
