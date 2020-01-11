package guis;

public class GuiTexture
{
    private int textureID;
    private int numberOfRows = 1;

    public GuiTexture(int textureID)
    {
        this.textureID = textureID;
    }

    public int GetTextureID() { return textureID; }
    public int GetNumberOfRows() { return numberOfRows; }
    public void SetNumberOfRows(int numberOfRows) { this.numberOfRows = numberOfRows; }
}
